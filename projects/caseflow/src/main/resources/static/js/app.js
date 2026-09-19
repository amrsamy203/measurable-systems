(() => {
  // Works at / locally and at /caseflow/ behind the public gateway.
  const BASE = (() => {
    const p = location.pathname;
    if (p === "/caseflow" || p.startsWith("/caseflow/")) return "/caseflow";
    return "";
  })();

  const state = {
    token: localStorage.getItem("caseflow_token") || "",
    user: JSON.parse(localStorage.getItem("caseflow_user") || "null"),
    pollTimer: null,
  };

  const $ = (id) => document.getElementById(id);

  async function api(path, options = {}) {
    const headers = Object.assign(
      { "Content-Type": "application/json" },
      options.headers || {}
    );
    if (state.token) headers.Authorization = `Bearer ${state.token}`;
    const res = await fetch(BASE + path, { ...options, headers });
    if (res.status === 401) {
      logout(false);
      throw new Error("Unauthorized");
    }
    const text = await res.text();
    const data = text ? JSON.parse(text) : null;
    if (!res.ok) throw new Error((data && data.error) || res.statusText);
    return data;
  }

  function showApp() {
    $("login-view").hidden = true;
    $("app-view").hidden = false;
    $("user-label").textContent = state.user
      ? `${state.user.displayName} · ${state.user.role}`
      : "";
    refreshAll();
    startPolling();
    loadArchitecture();
  }

  function showLogin() {
    $("login-view").hidden = false;
    $("app-view").hidden = true;
    stopPolling();
  }

  function logout(clearForm) {
    state.token = "";
    state.user = null;
    localStorage.removeItem("caseflow_token");
    localStorage.removeItem("caseflow_user");
    showLogin();
    if (clearForm !== false) $("login-error").hidden = true;
  }

  function startPolling() {
    stopPolling();
    state.pollTimer = setInterval(() => {
      refreshMetrics();
      refreshCases();
      refreshAgentsQueues();
    }, 1500);
  }

  function stopPolling() {
    if (state.pollTimer) clearInterval(state.pollTimer);
    state.pollTimer = null;
  }

  async function refreshMetrics() {
    try {
      const m = await api("/api/metrics/throughput");
      $("m-processed").textContent = m.eventsProcessed;
      $("m-eps").textContent = Number(m.eventsPerSecond).toFixed(2);
      $("m-p95").textContent = m.p95LatencyMs;
      $("m-depth").textContent = m.queueDepth;
      $("m-fail").textContent = m.failureCount;
      $("m-updated").textContent = m.lastUpdated
        ? new Date(m.lastUpdated).toLocaleTimeString()
        : "—";
    } catch (_) {
      /* ignore transient */
    }
  }

  async function refreshCases() {
    try {
      const status = $("status-filter").value;
      const q = status ? `?status=${encodeURIComponent(status)}` : "";
      const cases = await api(`/api/cases${q}`);
      const body = $("cases-body");
      body.innerHTML = "";
      if (!cases.length) {
        body.innerHTML = `<tr><td colspan="9" style="color:var(--muted)">No cases yet — simulate load to populate.</td></tr>`;
        return;
      }
      for (const c of cases.slice(0, 80)) {
        const tr = document.createElement("tr");
        const canStart = c.status === "ASSIGNED";
        const canResolve = c.status === "IN_PROGRESS";
        tr.innerHTML = `
          <td class="mono">${c.id}</td>
          <td>${escapeHtml(c.externalRef || "")}</td>
          <td>${escapeHtml(c.title || "")}</td>
          <td><span class="prio prio-${c.priority}">${c.priority}</span></td>
          <td><span class="status-pill status-${c.status}">${c.status}</span></td>
          <td>${escapeHtml(c.queueName || "—")}</td>
          <td>${escapeHtml(c.assigneeName || "—")}</td>
          <td class="mono" style="font-size:0.72rem">${c.slaDeadline ? new Date(c.slaDeadline).toLocaleString() : "—"}</td>
          <td></td>`;
        const actions = tr.lastElementChild;
        if (canStart) {
          const b = document.createElement("button");
          b.className = "btn tiny secondary";
          b.textContent = "Start";
          b.onclick = () => transition(c.id, "IN_PROGRESS");
          actions.appendChild(b);
        } else if (canResolve) {
          const b = document.createElement("button");
          b.className = "btn tiny primary";
          b.style.width = "auto";
          b.textContent = "Resolve";
          b.onclick = () => transition(c.id, "RESOLVED");
          actions.appendChild(b);
        }
        body.appendChild(tr);
      }
    } catch (_) {
      /* ignore */
    }
  }

  async function transition(id, status) {
    try {
      await api(`/api/cases/${id}/transition`, {
        method: "POST",
        body: JSON.stringify({ status }),
      });
      refreshCases();
    } catch (e) {
      alert(e.message);
    }
  }

  async function refreshAgentsQueues() {
    try {
      const [agents, queues] = await Promise.all([
        api("/api/agents"),
        api("/api/queues"),
      ]);
      $("agents-list").innerHTML = agents
        .map(
          (a) => `<li>
            <div><strong>${escapeHtml(a.displayName)}</strong><div class="meta">${[...(a.skills || [])].join(", ") || "—"}</div></div>
            <span class="meta">${a.openCaseCount} open</span>
          </li>`
        )
        .join("");
      $("queues-list").innerHTML = (queues.length
        ? queues
        : [{ name: "(empty)", depth: 0 }]
      )
        .map(
          (q) => `<li><span>${escapeHtml(q.name)}</span><span class="meta">${q.depth}</span></li>`
        )
        .join("");
    } catch (_) {
      /* ignore */
    }
  }

  async function loadArchitecture() {
    try {
      const arch = await api("/api/architecture");
      const root = $("arch-view");
      const comps = (arch.components || [])
        .map(
          (c) => `<article class="arch-card">
            <div class="type">${escapeHtml(c.type || "")}</div>
            <h3>${escapeHtml(c.name)}</h3>
            <p>${escapeHtml(c.description || "")}</p>
          </article>`
        )
        .join("");
      const adrs = (arch.adrs || [])
        .map(
          (a) => `<div class="adr">
            <strong>${escapeHtml(a.id)} — ${escapeHtml(a.title)}</strong>
            <span>${escapeHtml(a.decision || "")}</span>
          </div>`
        )
        .join("");
      const flows = (arch.flows || [])
        .map((f) => `<li class="meta" style="display:list-item;margin-left:1rem">${escapeHtml(f)}</li>`)
        .join("");
      root.innerHTML = `
        <p style="color:var(--muted);margin:0 0 0.5rem;font-size:0.85rem">${escapeHtml(arch.style || "")}</p>
        <div class="arch-components">${comps}</div>
        <h3 style="margin:0.5rem 0 0;font-size:0.9rem">ADRs</h3>
        ${adrs}
        <h3 style="margin:0.5rem 0 0;font-size:0.9rem">Flows</h3>
        <ul style="margin:0;padding:0">${flows}</ul>`;
    } catch (e) {
      $("arch-view").textContent = e.message;
    }
  }

  function refreshAll() {
    refreshMetrics();
    refreshCases();
    refreshAgentsQueues();
  }

  function escapeHtml(s) {
    return String(s)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  function flash(msg) {
    const el = $("demo-msg");
    el.hidden = false;
    el.textContent = msg;
    setTimeout(() => {
      el.hidden = true;
    }, 4000);
  }

  $("login-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    $("login-error").hidden = true;
    try {
      const data = await api("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({
          email: $("email").value.trim(),
          password: $("password").value,
        }),
      });
      state.token = data.token;
      state.user = data;
      localStorage.setItem("caseflow_token", data.token);
      localStorage.setItem("caseflow_user", JSON.stringify(data));
      showApp();
    } catch (err) {
      $("login-error").hidden = false;
      $("login-error").textContent = err.message || "Login failed";
    }
  });

  $("logout-btn").addEventListener("click", () => logout());
  $("refresh-cases").addEventListener("click", refreshCases);
  $("status-filter").addEventListener("change", refreshCases);

  $("simulate-btn").addEventListener("click", async () => {
    const count = Number($("sim-count").value) || 100;
    try {
      const res = await api("/api/demo/simulate", {
        method: "POST",
        body: JSON.stringify({ count }),
      });
      flash(`Enqueued ${res.enqueued} synthetic events`);
      setTimeout(refreshAll, 400);
    } catch (e) {
      flash(e.message);
    }
  });

  $("inject-btn").addEventListener("click", async () => {
    try {
      const res = await api("/api/demo/failure-injection", {
        method: "POST",
        body: JSON.stringify({
          slowProcessingMs: Number($("slow-ms").value) || 0,
          failurePercent: Number($("fail-pct").value) || 0,
        }),
      });
      flash(res.message);
    } catch (e) {
      flash(e.message);
    }
  });

  if (state.token && state.user) {
    showApp();
  } else {
    showLogin();
  }
})();
