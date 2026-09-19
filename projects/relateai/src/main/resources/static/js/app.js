(() => {
  // Works at / locally and at /relateai/ behind the public gateway.
  const BASE = (() => {
    const p = location.pathname;
    if (p === "/relateai" || p.startsWith("/relateai/")) return "/relateai";
    return "";
  })();

  const TOKEN_KEY = "relateai.token";
  const USER_KEY = "relateai.user";

  let token = localStorage.getItem(TOKEN_KEY);
  let user = JSON.parse(localStorage.getItem(USER_KEY) || "null");
  let challenge = null;
  let metricsTimer = null;

  const $ = (id) => document.getElementById(id);

  async function api(path, options = {}) {
    const headers = Object.assign({}, options.headers || {});
    if (!(options.body instanceof FormData)) {
      headers["Content-Type"] = headers["Content-Type"] || "application/json";
    }
    if (token) headers.Authorization = `Bearer ${token}`;
    const res = await fetch(BASE + path, { ...options, headers });
    if (res.status === 401) {
      logout(false);
      throw new Error("Unauthorized");
    }
    const text = await res.text();
    const data = text ? JSON.parse(text) : null;
    if (!res.ok) {
      throw new Error((data && data.error) || res.statusText);
    }
    return data;
  }

  function showLogin() {
    $("login-view").hidden = false;
    $("app-view").hidden = true;
    if (metricsTimer) clearInterval(metricsTimer);
  }

  function showApp() {
    $("login-view").hidden = true;
    $("app-view").hidden = false;
    $("user-label").textContent = `${user.displayName} · ${user.email}`;
    loadChallenge();
    loadGraph();
    loadMetrics();
    metricsTimer = setInterval(loadMetrics, 2000);
  }

  function logout(clearForm = true) {
    token = null;
    user = null;
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    showLogin();
    if (clearForm) {
      $("login-error").hidden = true;
    }
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
      token = data.token;
      user = data;
      localStorage.setItem(TOKEN_KEY, token);
      localStorage.setItem(USER_KEY, JSON.stringify(user));
      showApp();
    } catch (err) {
      $("login-error").textContent = err.message || "Login failed";
      $("login-error").hidden = false;
    }
  });

  $("logout-btn").addEventListener("click", () => logout());

  async function loadChallenge() {
    try {
      challenge = await api("/api/challenges/today");
      $("challenge-prompt").textContent = challenge.prompt;
      $("challenge-date").textContent = `Date: ${challenge.challengeDate}`;
      $("challenge-source").textContent = challenge.source;
      const responses = await api(`/api/challenges/${challenge.id}/responses`);
      renderResponses(responses);
    } catch (err) {
      $("challenge-prompt").textContent = err.message;
    }
  }

  function renderResponses(responses) {
    const el = $("responses-list");
    if (!responses.length) {
      el.innerHTML = `<p class="muted small">No responses yet.</p>`;
      return;
    }
    el.innerHTML = `<ul class="list">${responses
      .map((r) => `<li><span>${escapeHtml(r.body)}</span><span class="score">u${r.userId}</span></li>`)
      .join("")}</ul>`;
  }

  $("response-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    if (!challenge) return;
    const msg = $("challenge-msg");
    msg.hidden = true;
    try {
      await api(`/api/challenges/${challenge.id}/responses`, {
        method: "POST",
        body: JSON.stringify({ body: $("response-body").value }),
      });
      $("response-body").value = "";
      msg.textContent = "Response saved.";
      msg.hidden = false;
      loadChallenge();
      loadMetrics();
    } catch (err) {
      msg.textContent = err.message;
      msg.hidden = false;
      msg.style.color = "var(--danger)";
    }
  });

  async function loadGraph() {
    try {
      const [top, hottest, recs] = await Promise.all([
        api("/api/graph/top-connected?limit=5"),
        api("/api/graph/hottest-topics?limit=5"),
        api("/api/graph/recommendations?limit=5"),
      ]);
      renderScoredUsers($("top-connected"), top);
      renderScoredTopics($("hottest-topics"), hottest);
      renderScoredUsers($("recommendations"), recs);
    } catch (err) {
      console.error(err);
    }
  }

  function renderScoredUsers(el, items) {
    if (!items.length) {
      el.innerHTML = `<li class="muted">None yet</li>`;
      return;
    }
    el.innerHTML = items
      .map((u) => `<li><span>${escapeHtml(u.displayName)}</span><span class="score">${u.score}</span></li>`)
      .join("");
  }

  function renderScoredTopics(el, items) {
    if (!items.length) {
      el.innerHTML = `<li class="muted">None yet</li>`;
      return;
    }
    el.innerHTML = items
      .map((t) => `<li><span>${escapeHtml(t.name)}</span><span class="score">${t.score}</span></li>`)
      .join("");
  }

  $("refresh-graph").addEventListener("click", loadGraph);

  $("upload-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const file = $("upload-file").files[0];
    if (!file) return;
    const msg = $("upload-msg");
    msg.hidden = true;
    const form = new FormData();
    form.append("file", file);
    try {
      const data = await api("/api/media/upload", { method: "POST", body: form });
      msg.textContent = `Uploaded → ${data.publicPath}`;
      msg.style.color = "var(--teal)";
      msg.hidden = false;
      $("upload-preview").hidden = false;
      $("upload-img").src = data.publicPath;
      $("upload-link").href = data.publicPath;
      $("upload-link").textContent = data.publicPath;
      loadMetrics();
    } catch (err) {
      msg.textContent = err.message;
      msg.style.color = "var(--danger)";
      msg.hidden = false;
    }
  });

  async function loadMetrics() {
    try {
      const m = await api("/api/metrics/throughput");
      $("m-challenges").textContent = m.challengesGenerated;
      $("m-interactions").textContent = m.interactionsRecorded;
      $("m-follows").textContent = m.followsCreated;
      $("m-joins").textContent = m.topicJoins;
      $("m-responses").textContent = m.challengeResponses;
      $("m-uploads").textContent = m.uploads;
      $("m-updated").textContent = m.lastUpdated;
    } catch (_) {
      /* ignore while logged out */
    }
  }

  function escapeHtml(str) {
    return String(str)
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;");
  }

  if (token && user) {
    showApp();
  } else {
    showLogin();
  }
})();
