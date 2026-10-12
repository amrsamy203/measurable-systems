# Measurable Systems

### Backend engineering portfolio by [Amr Samy](https://www.linkedin.com/in/amr-samy-2851ab13b/)

[![CI](https://github.com/amrsamy203/measurable-systems/actions/workflows/ci.yml/badge.svg)](https://github.com/amrsamy203/measurable-systems/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-green?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?logo=postgresql&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.13-FF6600?logo=rabbitmq&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)

Java / Spring Boot systems that show **throughput, routing, failure handling and integrations** — each one exposes its own metrics, so the claims can be checked rather than taken on trust.

**Portfolio site:** [amrsamy203.github.io](https://amrsamy203.github.io)

---

## Why these projects

They re-create, in clean open code, the kinds of problems I've solved in production:

| Production result (anonymized) | Re-created in |
|---|---|
| AML alert-generation pipeline cut from **~45 min to ~2 min** with Java concurrency, plus rules-based round-robin routing of cases across queues | [CaseFlow](projects/caseflow) |
| Telecom campaign systems sending **20M+ SMS per campaign** | [DispatchGrid](projects/dispatchgrid) — rate limits, retries, outbox, DLQ |
| OpenAI-powered daily challenges and graph analysis of user connections | [RelateAI](projects/relateai) |

---

## Projects

| Project | Problem | Highlights | Stack |
|---|---|---|---|
| **[CaseFlow](projects/caseflow)** | Investigation cases pile up without fair assignment | Ingest → rule evaluation → priority queues → round-robin assignment → SLA → audit trail · live Throughput Console (events/sec, p95, queue depth) | Spring Boot, PostgreSQL, RabbitMQ, JWT |
| **[DispatchGrid](projects/dispatchgrid)** | Outbound SMS/email/webhooks fail under provider rate limits | Transactional outbox · token-bucket rate limiting · retries with backoff · dead-letter queue · delivery receipts · per-tenant quotas · failure injection | Spring Boot, PostgreSQL, RabbitMQ, JWT + API keys |
| **[RelateAI](projects/relateai)** | Adding AI features without graph context or storage discipline | Social/topic graph insights · daily AI challenges (OpenAI with deterministic mock fallback) · media uploads | Spring Boot, PostgreSQL, OpenAI |

Each project has a requirements doc, a system design doc and a case study:

| | Requirements | System design | Case study |
|---|---|---|---|
| CaseFlow | [01](docs/caseflow/01-requirements.md) | [02](docs/caseflow/02-system-design.md) | [03](docs/caseflow/03-case-study.md) |
| DispatchGrid | [01](docs/dispatchgrid/01-requirements.md) | [02](docs/dispatchgrid/02-system-design.md) | [03](docs/dispatchgrid/03-case-study.md) |
| RelateAI | [01](docs/relateai/01-requirements.md) | [02](docs/relateai/02-system-design.md) | [03](docs/relateai/03-case-study.md) |

### Server-rendered websites (Spring Boot + Thymeleaf)

[`mep-websites/`](mep-websites) is a Maven multi-module build with three websites for **fictional** engineering companies. Each has its own brand, server-side form validation, a README and automated tests.

| Website | Highlights |
|---|---|
| **[Meridian MEP Contracting](mep-websites/meridian-contracting)** | Sector-filtered project portfolio · service pages · validated RFQ form |
| **[Axis Engineering Consultants](mep-websites/axis-consultants)** | Editorial case studies · configurable design-fee estimator · project brief form |
| **[Thermiq HVAC Services](mep-websites/thermiq-hvac)** | Online booking with capacity rules (JPA + H2) · AC sizing calculator · plan pricing |

---

## Quick start (Docker)

Requires [Docker Desktop](https://www.docker.com/products/docker-desktop/).

```bash
docker compose up --build -d
```

| Service | URL | Demo login |
|---|---|---|
| CaseFlow | http://localhost:8081 | `admin@caseflow.demo` / `password` |
| DispatchGrid | http://localhost:8082 | `admin@dispatchgrid.demo` / `password` |
| RelateAI | http://localhost:8083 | `user1@relateai.demo` / `password` |
| Meridian (website) | http://localhost:8091/mep/meridian/ | — |
| Axis (website) | http://localhost:8092/mep/axis/ | — |
| Thermiq (website) | http://localhost:8093/mep/thermiq/ | — |
| RabbitMQ UI | http://localhost:15672 · http://localhost:15673 | `caseflow` / `caseflow` · `dispatchgrid` / `dispatchgrid` |

Swagger UI for each API is at `/swagger-ui.html`.

```bash
docker compose down       # stop
docker compose down -v    # stop + wipe volumes
```

## Run without Docker

Requires Java 21 and Maven 3.9+. The `local` profile uses H2 and an in-process async pipeline, so nothing else is needed.

```bash
cd projects/caseflow && mvn spring-boot:run      # same for dispatchgrid, relateai
mvn -B test                                      # run a project's tests
```

For the websites, see [mep-websites/README.md](mep-websites/README.md#run).

---

## Engineering practices

- Modular monolith with clean package boundaries
- REST + OpenAPI (springdoc)
- JWT auth (plus API keys on DispatchGrid)
- Async pipelines: `@Async` locally, RabbitMQ under Docker
- Actuator health checks and per-demo metrics endpoints
- Unit tests for domain rules (routing, rate limiting, graph), run in [CI](.github/workflows/ci.yml) on every push

## Repository layout

```text
├── projects/
│   ├── caseflow/          # Case routing + Throughput Console
│   ├── dispatchgrid/      # Multi-provider dispatch orchestrator
│   └── relateai/          # Graph insights + AI challenges
├── mep-websites/          # Spring Boot + Thymeleaf websites (Maven multi-module)
├── docs/                  # Requirements, system design and case studies per project
├── docker-compose.yml     # Full demo stack
└── .github/workflows/     # CI: tests for every module
```

---

## License

MIT — see [LICENSE](LICENSE). Demo credentials are for local use only; change secrets before any public deploy.
