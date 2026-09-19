# Measurable Systems

### Backend portfolio by [Amr Samy](mailto:samyamr270@gmail.com)

Java / Spring Boot systems built to demonstrate **real throughput, routing, and integrations** — not tutorial CRUD.

> I build backend systems that turn slow, fragile business processes into fast, reliable pipelines — queues, rules, integrations, and APIs that hold up under real volume.

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white" />
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.3-green?logo=springboot&logoColor=white" />
  <img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-16-336791?logo=postgresql&logoColor=white" />
  <img alt="RabbitMQ" src="https://img.shields.io/badge/RabbitMQ-3.13-FF6600?logo=rabbitmq&logoColor=white" />
  <img alt="Docker" src="https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white" />
  <img alt="Next.js" src="https://img.shields.io/badge/Next.js-14-black?logo=nextdotjs&logoColor=white" />
</p>

---

## Why this repo exists

Freelance clients need proof that you can design backends that **survive volume and failure**.  
This monorepo packages three production-style demos plus a personal site under one theme: **Measurable Systems** — every demo exposes metrics, architecture notes, and failure injection.

| Career proof (anonymized) | Shown by |
|---|---|
| Processing pipeline **~45 min → ~2 min** | [CaseFlow](projects/caseflow) concurrency & routing patterns |
| Campaign systems **20M+ messages** | [DispatchGrid](projects/dispatchgrid) rate limits, retries, DLQ |

---

## Flagship projects

| Project | Problem it solves | Highlights | Stack |
|---|---|---|---|
| **[CaseFlow](projects/caseflow)** | Ops/investigation cases pile up without fair assignment | Ingest → rules → queues → round-robin assign · SLA · audit · Throughput Console | Spring Boot, PostgreSQL, RabbitMQ, JWT |
| **[DispatchGrid](projects/dispatchgrid)** | Outbound SMS/email/webhooks fail under rate limits | Outbox · token-bucket limits · retries/DLQ · fake providers · API keys | Spring Boot, PostgreSQL, RabbitMQ |
| **[RelateAI](projects/relateai)** | “Add AI” without graph context or storage discipline | Topic/social graph insights · daily AI challenges · blob uploads | Spring Boot, PostgreSQL, OpenAI/mock |

**Portfolio site:** [portfolio-site/](portfolio-site) — Next.js “Measurable Systems” landing + case studies.

---

## Quick start (Docker — recommended)

Requires [Docker Desktop](https://www.docker.com/products/docker-desktop/).

```bash
docker compose up --build -d
```

| Service | URL | Demo login |
|---|---|---|
| Portfolio | http://localhost:3000 | — |
| CaseFlow | http://localhost:8081 | `admin@caseflow.demo` / `password` |
| DispatchGrid | http://localhost:8082 | `admin@dispatchgrid.demo` / `password` |
| RelateAI | http://localhost:8083 | `user1@relateai.demo` / `password` |

Full guide: [docs/DOCKER.md](docs/DOCKER.md)

```bash
docker compose down       # stop
docker compose down -v    # stop + wipe volumes
```

---

## Repository layout

```text
├── projects/
│   ├── caseflow/          # Case routing + Throughput Console
│   ├── dispatchgrid/      # Multi-provider dispatch orchestrator
│   └── relateai/          # Graph insights + AI challenges
├── portfolio-site/        # Next.js personal brand site
├── docs/                  # Design docs, brand, freelancing playbooks
├── scripts/               # Local Windows helpers (JDK/Maven PATH)
├── docker-compose.yml     # Full stack
└── .github/workflows/ci.yml
```

Each project has its own `README.md`, `Dockerfile`, and optional standalone `docker-compose.yml`.

---

## Architecture theme

```text
Visitor → Portfolio site
            ├── CaseFlow      → Throughput Console + Architecture Explorer
            ├── DispatchGrid  → Failure injection + metrics
            └── RelateAI      → Graph insights + AI feature API
```

Shared engineering practices across demos:

- Modular monolith (clean package boundaries)
- REST + OpenAPI / springdoc
- JWT (and API keys on DispatchGrid)
- Async pipelines (`@Async` local · RabbitMQ docker)
- Actuator health + demo metrics endpoints
- Unit tests for domain rules (routing, rate limit, graph)

---

## Local run (without Docker)

**Prerequisites:** Java 21, Maven 3.9+, Node 20+

```powershell
. .\scripts\dev-env.ps1
.\scripts\smoke-demos.ps1          # unit tests
.\scripts\run-caseflow.ps1         # :8081
.\scripts\run-dispatchgrid.ps1     # :8082
.\scripts\run-relateai.ps1         # :8083
.\scripts\run-portfolio.ps1        # :3000
```

---

## Documentation

| Area | Links |
|---|---|
| Strategy & brand | [constraints](docs/constraints.md) · [positioning](docs/brand/positioning.md) · [services](docs/brand/services-packages.md) |
| Case studies | [CaseFlow](docs/caseflow/03-case-study.md) · [DispatchGrid](docs/dispatchgrid/03-case-study.md) · [RelateAI](docs/relateai/03-case-study.md) |
| Freelancing | [profiles](docs/brand/platform-profiles.md) · [proposals](docs/freelancing/proposals-and-outreach.md) · [paste-ready](docs/freelancing/paste-ready-profiles.md) |
| Deploy | [Docker](docs/DOCKER.md) · [render.yaml](render.yaml) · [go-live](docs/freelancing/go-live-checklist.md) |

---

## Tech focus (what I freelance on)

1. Backend & API development (Java / Spring Boot)  
2. Workflow / queue systems (RabbitMQ, workers, DLQ)  
3. System integrations (REST, providers, messaging)  
4. High-throughput / batch backends  
5. AI feature endpoints (not “AI engineer” branding)

Cairo, Egypt · Open to Upwork / Khamsat / Freelancer engagements.

---

## License

MIT — see [LICENSE](LICENSE). Demo credentials are for local/portfolio use only; change secrets before any public deploy.
