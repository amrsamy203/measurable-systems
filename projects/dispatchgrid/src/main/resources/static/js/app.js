(() => {
  // Works at / locally and at /dispatchgrid/ behind the public gateway.
  const BASE = (() => {
    const p = location.pathname;
    if (p === "/dispatchgrid" || p.startsWith("/dispatchgrid/")) return "/dispatchgrid";
    return "";
  })();

  const state = {
    token: localStorage.getItem("dispatchgrid_token") || "",
    user: JSON.parse(localStorage.getItem("dispatchgrid_user") || "null"),
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
    if (!res.ok) throw new Error((data && (data.detail || data.error)) || res.statusText);
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
    loadDemoInfo();
  }

  function showLogin() {
    $("login-view").hidden = false;
    $("app-view").hidden = true;
    stopPolling();
  }

  function logout(clearForm) {
    state.token = "";
    state.user = null;
    localStorage.removeItem("dispatchgrid_token");
    localStorage.removeItem("dispatchgrid_user");
    showLogin();
    if (clearForm !== false) $("login-error").hidden = true;
  }

  function startPolling() {
    stopPolling();
    state.pollTimer = setInterval(() => {
      refreshMetrics();
      refreshCampaigns();
    }, 1500);
  }

  function stopPolling() {
    if (state.pollTimer) clearInterval(state.pollTimer);
    state.pollTimer = null;
  }

  async function refreshMetrics() {
    try {
      const m = await api("/api/metrics/throughput");
      $("m-sent").textContent = m.messagesSent;
      $("m-sps").textContent = Number(m.sentPerSecond).toFixed(2);
      $("m-p95").textContent = m.p95LatencyMs;
      $("m-depth").textContent = m.queueDepth;
      $("m-fail").textContent = m.failureCount;
      $("m-updated").textContent = m.lastUpdated
        ? new Date(m.lastUpdated).toLocaleTimeString()
        : "—";
    } catch (_) {
      /* ignore */
    }
  }

  async function refreshCampaigns() {
    try {
      const campaigns = await api("/api/campaigns");
      const body = $("campaigns-body");
      body.innerHTML = "";
      if (!campaigns.length) {
        body.innerHTML = `<tr><td colspan="8" style="color:var(--muted)">No campaigns yet — simulate to populate.</td></tr>`;
        return;
      }
      for (const c of campaigns.slice(0, 40)) {
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td class="mono">${c.id}</td>
          <td>${escapeHtml(c.name || "")}</td>
          <td class="mono">${c.channel}</td>
          <td><span class="status-pill status-${c.status}">${c.status}</span></td>
          <td class="mono">${c.recipientCount}</td>
          <td class="mono">${c.sentCount}</td>
          <td class="mono">${c.failedCount}</td>
          <td class="mono" style="font-size:0.72rem">${c.startedAt ? new Date(c.startedAt).toLocaleString() : "—"}</td>`;
        body.appendChild(tr);
      }
    } catch (_) {
      /* ignore */
    }
  }

  async function loadArchitecture() {
    try {
      const arch = await api("/api/architecture");
      const el = $("arch-content");
      const components = (arch.components || [])
        .map((c) => `<li><strong>${escapeHtml(c.name)}</strong> — ${escapeHtml(c.role)}</li>`)
        .join("");
      const adrs = (arch.adrs || [])
        .map(
          (a) =>
            `<li><strong>${escapeHtml(a.id)}: ${escapeHtml(a.title)}</strong><br/>${escapeHtml(a.decision)} <em>(${escapeHtml(a.why)})</em></li>`
        )
        .join("");
      const runtime = arch.runtime || {};
      el.innerHTML = `
        <p><strong>${escapeHtml(arch.product || "DispatchGrid")}</strong> · profile <code class="mono">${escapeHtml(String(runtime.activeProfiles || ""))}</code></p>
        <h3>Components</h3>
        <ul>${components}</ul>
        <h3>ADRs</h3>
        <ul>${adrs}</ul>
        <h3>Providers</h3>
        <ul>${Object.entries(runtime.providers || {})
          .map(([ch, key]) => `<li><strong>${escapeHtml(ch)}</strong> → ${escapeHtml(key)}</li>`)
          .join("")}</ul>`;
    } catch (e) {
      $("arch-content").innerHTML = `<p class="error">${escapeHtml(e.message)}</p>`;
    }
  }

  async function loadDemoInfo() {
    try {
      const info = await api("/api/demo/info");
      if (info.demoApiKey) $("demo-api-key").textContent = info.demoApiKey;
    } catch (_) {
      /* ignore */
    }
  }

  function refreshAll() {
    refreshMetrics();
    refreshCampaigns();
  }

  function showMsg(text, isError) {
    const el = $("demo-msg");
    el.hidden = false;
    el.textContent = text;
    el.style.color = isError ? "var(--danger)" : "var(--teal)";
  }

  function escapeHtml(s) {
    return String(s)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  $("login-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    $("login-error").hidden = true;
    try {
      const data = await api("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({
          email: $("email").value,
          password: $("password").value,
        }),
      });
      state.token = data.token;
      state.user = data;
      localStorage.setItem("dispatchgrid_token", data.token);
      localStorage.setItem("dispatchgrid_user", JSON.stringify(data));
      showApp();
    } catch (err) {
      $("login-error").hidden = false;
      $("login-error").textContent = err.message || "Login failed";
    }
  });

  $("logout-btn").addEventListener("click", () => logout(true));
  $("refresh-campaigns").addEventListener("click", refreshCampaigns);

  $("simulate-btn").addEventListener("click", async () => {
    try {
      const count = Number($("sim-count").value) || 500;
      const channel = $("sim-channel").value;
      const result = await api("/api/demo/simulate-campaign", {
        method: "POST",
        body: JSON.stringify({ channel, count }),
      });
      showMsg(`Started campaign #${result.id} (${result.channel} × ${result.recipientCount})`);
      refreshAll();
    } catch (err) {
      showMsg(err.message, true);
    }
  });

  $("inject-btn").addEventListener("click", async () => {
    try {
      await api("/api/demo/failure-injection", {
        method: "POST",
        body: JSON.stringify({
          providerKey: $("inject-provider").value,
          slowMs: Number($("slow-ms").value) || 0,
          failPercent: Number($("fail-pct").value) || 0,
        }),
      });
      showMsg("Failure injection applied");
    } catch (err) {
      showMsg(err.message, true);
    }
  });

  if (state.token && state.user) {
    showApp();
  } else {
    showLogin();
  }
})();
