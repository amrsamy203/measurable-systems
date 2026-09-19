# Amr Samy — Portfolio

Personal portfolio for **Amr Samy** — Measurable Systems. Industrial dark UI for backend / queue / integration work.

## Stack

- Next.js 14 (App Router)
- TypeScript
- CSS Modules + design tokens in `app/globals.css`
- IBM Plex Sans + IBM Plex Mono via `next/font`

## Run locally

Node should be available (this project was built expecting Node at `C:\Program Files\nodejs\`).

```bash
cd "c:\Freelance portofoli and projects\portfolio-site"
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000).

```bash
npm run build
npm start
```

## Structure

```
portfolio-site/
├── app/
│   ├── layout.tsx          # fonts, shell
│   ├── page.tsx            # homepage sections
│   ├── globals.css         # tokens + base
│   └── work/[slug]/page.tsx  # CaseFlow, DispatchGrid, RelateAI
├── components/             # Hero, work, services, about, labs, contact
├── lib/content.ts          # copy + project data
└── package.json
```

### Homepage sections

1. **Hero** — brand-first, topology SVG, Contact / View work
2. **Selected work** — outcome-first project list
3. **Services** — package briefs
4. **About** — experience + metric counters
5. **Labs** — Measurable Systems + queue pulse
6. **Contact** — email / phone / Cairo

### Case studies

- `/work/caseflow`
- `/work/dispatchgrid`
- `/work/relateai`
