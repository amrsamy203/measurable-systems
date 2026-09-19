# CaseFlow — Case Study

## Problem

Ops and investigation teams often ingest a firehose of alerts/events that must become **workable cases**: routed by priority and skill, assigned fairly across agents, tracked against SLAs, and auditable. Many portfolio backends stop at “REST + CRUD.” CaseFlow demonstrates the harder parts — **queues, rules, fair assignment, and measurable throughput** — in a runnable modular monolith.

## What I built

A Spring Boot 3 / Java 21 MVP that:

1. **Ingests** events into cases (`NEW`)
2. **Evaluates routing rules** (priority + skill → named queue + SLA)
3. **Assigns** via round-robin among skill-eligible agents
4. **Audits** every meaningful state change
5. Exposes a **Throughput Console** (events processed, events/sec, p95 latency, queue depth, failures)
6. Supports **failure injection** (artificial delay / failure %) for live demos
7. Serves a polished **ops dashboard** (login, board, simulate, architecture explorer)

### Architecture choices (ADRs)

| ADR | Decision | Why |
|-----|----------|-----|
| Modular monolith | Hexagonal-ish packages in one deployable | Portfolio clarity without microservices sprawl |
| Profile-split messaging | `@Async` on `local`; RabbitMQ on `docker` | Demo runs without Docker; Compose shows real AMQP |
| PostgreSQL (+ H2 local) | Transactional assignment + audit + `@Version` | Enough concurrency control at demo scale |
| Round-robin + skills | Oldest `lastAssignedAt` among eligible agents | Fair, explainable, CV-aligned |
| REST + OpenAPI | No GraphQL | Sufficient for board + metrics |

### Pipeline

```
POST /api/events
    → persist Case (NEW) + audit CREATED
    → enqueue (async or RabbitMQ ingest)
    → RoutingMatcher → QUEUED + SLA + audit ROUTED
    → RoundRobinSelector → ASSIGNED + audit ASSIGNED
    → agent transition → IN_PROGRESS → RESOLVED
```

Docker profile queues: `caseflow.ingest` → `caseflow.assign`, with DLQ `caseflow.dlq`.

## How to evaluate it

```bash
cd projects/caseflow
mvn spring-boot:run
# open http://localhost:8080 — admin@caseflow.demo / password
# Simulate 100 events → watch metrics and board
```

Or: `docker compose up --build` for Postgres + RabbitMQ.

## Outcomes this demo is designed to show

- **Rule-based routing** is visible (queue names differ by priority/skill)
- **Fair assignment** spreads work across seeded agents
- **Throughput is measurable** under synthetic load
- **Failure modes** can be injected without redeploying
- **Architecture is documented in-product** via `/api/architecture`

## Honest scope

This is an MVP/portfolio engine — not a production AML platform. Multi-tenant billing, SSO, ML matching, and Kafka are intentionally out of scope (see requirements). The point is to show how I design **workflow + queue backends** that hold up under volume and explain their trade-offs.

## Related docs

- [01 — Requirements](./01-requirements.md)
- [02 — System design](./02-system-design.md)
