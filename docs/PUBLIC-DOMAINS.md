# Public addresses

| What | Address | Runs on | Always online? |
|---|---|---|---|
| Portfolio | https://amrsamy203.github.io | GitHub Pages (repo `amrsamy203/amrsamy203.github.io`, built files only) | Yes |
| Live demos + APIs | https://edgy-bagged-overheat.ngrok-free.dev/{caseflow,dispatchgrid,relateai}/ | This PC → gateway :3099 → ngrok service | Only while the PC and Docker are up |
| Website demos | https://edgy-bagged-overheat.ngrok-free.dev/mep/{meridian,axis,thermiq}/ | Same gateway and tunnel | Only while the PC and Docker are up |

The portfolio calls the demo APIs cross-origin (status checks, Live lab load test). `gateway/nginx.conf` owns CORS and only allows the portfolio origins listed in its `$cors_origin` map — add any new portfolio domain there and restart the gateway (`docker compose restart gateway`). When the PC is off, the portfolio stays up and shows the demos as "asleep".

ngrok's free plan cannot use a custom domain (only the assigned `*.ngrok-free.dev` name), so a nicer name for the demos needs the Cloudflare option below.

## Republish the portfolio

Source stays in the private `amrsamy203/portfolio-site` repo. After changing it:

```powershell
cd portfolio-site
.\scripts\deploy-pages.ps1
```

It builds the static export in Docker and pushes only `out/` to the Pages repo. To refresh the local gateway copy too: `docker compose up --build -d portfolio`.

## Option 1 — `amrsamy.is-a.dev` for the portfolio (free, ~days for review)

[is-a.dev](https://is-a.dev) hands out free subdomains for developer portfolios via a pull request. `amrsamy` and `amr-samy` were both unregistered on 2026-10-07. Their maintainers ask that requests are written by the person registering, so the PR itself is yours to open:

1. Done: fork `amrsamy203/register`, branch `amrsamy`, with [`domains/amrsamy.json`](https://github.com/amrsamy203/register/blob/amrsamy/domains/amrsamy.json):

   ```json
   {
     "owner": {
       "username": "amrsamy203",
       "email": "samyamr270@gmail.com"
     },
     "records": {
       "CNAME": "amrsamy203.github.io"
     }
   }
   ```

2. Open the PR from https://github.com/is-a-dev/register/compare/main...amrsamy203:register:amrsamy?expand=1 — tick the template checkboxes you agree with, write the "Website Purpose" in your own words with a link to https://amrsamy203.github.io, and answer review comments. Don't @-mention maintainers (it gets the PR deprioritised).
3. After it is merged:
   - In `portfolio-site/lib/content.ts` set `site.url` to `https://amrsamy.is-a.dev`.
   - Publish with the domain: `.\scripts\deploy-pages.ps1 -CustomDomain amrsamy.is-a.dev` (writes the `CNAME` file GitHub Pages uses).
   - In the Pages repo → Settings → Pages, tick **Enforce HTTPS** once the certificate is issued.
   - Optional but recommended: verify the domain under your GitHub profile → Settings → Pages (this needs a second is-a.dev PR with the TXT record GitHub gives you; see https://docs.is-a.dev/guides/github-pages/).

`amrsamy203.github.io` keeps working and redirects to the new name.

## Option 2 — Cloudflare Tunnel + free domain for everything

Gives the demos (and optionally the portfolio) your own name, removes the ngrok "Visit Site" warning and the 20k requests/month cap.

You do (needs your accounts):

1. Register a free domain at https://dash.domain.digitalplat.org (DigitalPlat FreeDomain, e.g. `amrsamy.dpdns.org`; limit 3 per account).
2. Create a free Cloudflare account, **Add a domain** → enter it, pick the Free plan, copy the two nameservers Cloudflare assigns.
3. Paste those nameservers into the domain's settings at DigitalPlat. Wait until Cloudflare shows the domain as **Active** (minutes to a few hours).
4. Cloudflare dashboard → Zero Trust → Networks → Tunnels → **Create a tunnel** → Cloudflared, name it `measurable-systems`. Copy the token from the install command it shows. Keep it private (never paste it into chats or commits).
5. On the tunnel's **Public hostname** tab add: subdomain `demos`, your domain, service type `HTTP`, URL `localhost:3099`.

Then on this PC (UAC prompt; the token is asked for in a hidden prompt):

```powershell
.\scripts\cloudflare-tunnel.ps1 install
.\scripts\cloudflare-tunnel.ps1 status -Hostname demos.<domain>   # all three apps should answer 200
.\scripts\cloudflare-tunnel.ps1 retire-ngrok                      # only after the links work
```

`restore-ngrok` rolls back. Finally point `site.liveUrl` in `portfolio-site/lib/content.ts` at `https://demos.<domain>` and run `deploy-pages.ps1`. No gateway CORS change is needed — CORS lists the *portfolio's* origin, which stays the same.
