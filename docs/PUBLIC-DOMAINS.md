# Public addresses

| What | Address | Runs on | Always online? |
|---|---|---|---|
| Portfolio | https://amrsamy203.github.io | GitHub Pages (repo `amrsamy203/amrsamy203.github.io`, built files only) | Yes |
| Live demos + APIs | https://edgy-bagged-overheat.ngrok-free.dev/{caseflow,dispatchgrid,relateai}/ | This PC → gateway :3099 → ngrok service | Only while the PC and Docker are up |

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

[is-a.dev](https://is-a.dev) hands out free subdomains for developer portfolios via a pull request. `amrsamy` and `amr-samy` were both unregistered on 2026-10-07. Their maintainers ask that requests are written by the person registering, so submit this yourself:

1. Fork https://github.com/is-a-dev/register and add `domains/amrsamy.json`:

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

2. Open the pull request, fill in their template (they ask for a link/screenshot of the live site — use https://amrsamy203.github.io), and answer any review comments.
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
4. Cloudflare dashboard → Networking → Tunnels → **Create a tunnel** (cloudflared) → copy the tunnel token. Keep it private.

Then hand it over and the remaining steps are scripted on this PC:

- Install `cloudflared` as the Windows service (`cloudflared service install <token>`) and stop/uninstall the ngrok service.
- Route `demos.<domain>` → `http://localhost:3099` (published application on the tunnel).
- Point `site.liveUrl` at `https://demos.<domain>`, drop the ngrok-specific header, add the new origin to the gateway CORS map, republish.
- Optionally serve the portfolio at the apex/`www` through Cloudflare Pages or keep GitHub Pages with a CNAME.
