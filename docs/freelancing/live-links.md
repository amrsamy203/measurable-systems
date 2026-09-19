# Live links

## Portfolio (temporary Vercel — claim within ~60 minutes)

- **Live:** https://temporary-swift-nebula-ysdu6bj.vercel.app
- **Claim to keep:** https://vercel.com/claim-deployment?code=2b32cefa-a80f-4df1-bd18-28bc03e33193

After claiming, run a permanent deploy:

```powershell
. .\scripts\dev-env.ps1
cd portfolio-site
npx vercel login
npm run build
cd out
npx vercel --prod
```

Or drag `portfolio-site/out` to https://app.netlify.com/drop

## Local demos (verified 2026-09-18)

| App | URL | Credentials | Smoke result |
|---|---|---|---|
| CaseFlow | http://localhost:8081 | admin@caseflow.demo / password | simulate 25 → metrics OK |
| DispatchGrid | http://localhost:8082 | admin@dispatchgrid.demo / password | simulate 40 SMS → sent=40 |
| RelateAI | http://localhost:8083 | user1@relateai.demo / password | graph + challenges OK |

## Cloud demos (pending your Render account)

Use root `render.yaml` Blueprint after pushing to GitHub. Then fill:

- CaseFlow: `https://____________.onrender.com`
- DispatchGrid: `https://____________.onrender.com`
- RelateAI: `https://____________.onrender.com`
