# Go-live checklist (practical)

## Already done in this workspace

- [x] Java 21 detected (`Microsoft JDK 21`)
- [x] Portable Maven in `.tools/apache-maven-3.9.9`
- [x] Unit tests pass: CaseFlow, DispatchGrid, RelateAI
- [x] CaseFlow live smoke: login → simulate 25 → metrics + cases OK (`:8081`)
- [x] Run scripts under `scripts/`
- [x] `render.yaml` for one-click Render Blueprint demos
- [x] Dockerfiles default to `local` profile (H2, no external DB required)
- [x] Git installed + repo initialized

## You must complete (needs your browser login)

### 1) Portfolio on Vercel (~5 min)

```powershell
. .\scripts\dev-env.ps1
cd portfolio-site
npx vercel login
npx vercel --prod
```

Paste the resulting URL into `portfolio-site/lib/content.ts` (`site.liveUrl`) and into Upwork.

### 2) Demo apps on Render (~15–20 min)

1. Push this repo to GitHub (or import from local via Render).
2. Render Dashboard → **New** → **Blueprint** → select repo → `render.yaml`.
3. Deploy `caseflow`, `dispatchgrid`, `relateai`.
4. Copy the three `*.onrender.com` URLs into `docs/freelancing/live-links.md` and `content.ts`.

> Free Render dynos sleep when idle — fine for portfolio; mention “wake may take ~30s” in proposals.

### 3) Platform profiles (copy/paste)

Use files in:

- `docs/brand/platform-profiles.md`
- `docs/freelancing/khamsat-services.md`
- `docs/freelancing/proposals-and-outreach.md`
- `docs/freelancing/paste-ready-profiles.md` (final with placeholders)

### 4) First week bidding

- 5–10 Upwork proposals using the template
- Publish 1–2 Khamsat services
- Target: Spring Boot / API / queue / SMS integration jobs only

## Local demo URLs (working now)

| App | URL | Login |
|---|---|---|
| Portfolio | http://localhost:3000 | — |
| CaseFlow | http://localhost:8081 | admin@caseflow.demo / password |
| DispatchGrid | http://localhost:8082 | admin@dispatchgrid.demo / password |
| RelateAI | http://localhost:8083 | user1@relateai.demo / password |

```powershell
. .\scripts\dev-env.ps1
.\scripts\run-portfolio.ps1      # terminal 1
.\scripts\run-caseflow.ps1       # terminal 2
.\scripts\run-dispatchgrid.ps1   # terminal 3
.\scripts\run-relateai.ps1       # terminal 4
```
