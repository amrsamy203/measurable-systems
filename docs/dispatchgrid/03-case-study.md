# DispatchGrid — Case Study

## Problem

High-volume messaging platforms are not “CRUD + send.” They must **orchestrate** multi-provider traffic under **rate limits**, survive **transient failures** with retries, park poison messages in a **DLQ**, and prove **delivery** with receipts — while ops teams watch **throughput**. Many portfolio backends stop at a single HTTP client call. DispatchGrid demonstrates the harder orchestration layer.

## What I built

A Spring Boot 3 / Java 21 MVP that:

1. Accepts **campaigns** (or machine `/api/send`) for SMS / email / webhook  
2. Writes work to an **outbox** in the same transaction as message rows  
3. Dispatches via **strategy providers** (`FakeSms`, `FakeEmail`, `FakeWebhook`)  
4. Applies **token-bucket** rate limits per provider  
5. **Retries with backoff**; exhausted attempts become `FAILED_DLQ`  
6. Records **delivery receipts** (simulated DELIVERED / FAILED)  
7. Exposes a **Throughput Console** (sent, sent/sec, p95, outbox depth, failures)  
8. Supports **failure injection** and documents **ADRs** via `/api/architecture`  
9. Serves a dark industrial **ops dashboard** (JWT login + demo API key)

### Architecture choices (ADRs)

| ADR | Decision | Why |
|-----|----------|-----|
| Modular monolith | Hexagonal-ish packages in one deployable | Portfolio clarity without microservices sprawl |
| Outbox | `outbox_messages` drained by workers | Crash-safe pending sends; CV-aligned reliability pattern |
| Token-bucket | In-memory per provider | Shows throttling without Redis; swappable later |
| Dual auth | JWT + `X-Api-Key` | Mirrors real messaging ops vs machine send |
| Profile-split | `@Async`+H2 local; Postgres+Rabbit docker | Demo runs without Docker; Compose shows AMQP |

### Pipeline

```
POST /api/demo/simulate-campaign
    → campaign + N messages + outbox rows
    → enqueue (@Async or RabbitMQ)
    → rate limit → Fake*Provider.send()
    → SENT + delivery receipt → DELIVERED/FAILED
    → or retry / FAILED_DLQ
```

## How to evaluate it

```bash
cd projects/dispatchgrid
mvn spring-boot:run
# open http://localhost:8080 — admin@dispatchgrid.demo / password
# Simulate SMS × 500 → watch metrics and campaigns
```

Or: `docker compose up --build` for Postgres + RabbitMQ.

Machine send:

```bash
curl -X POST http://localhost:8080/api/send \
  -H "Content-Type: application/json" \
  -H "X-Api-Key: dg_demo_sk_live_7f3a9c2e8b1d4f6a" \
  -d '{"channel":"EMAIL","destination":"ops@example.com","body":"ping"}'
```

## Outcomes this demo is designed to show

- **Outbox-based dispatch** is visible (queue depth moves then drains)  
- **Provider strategy** is selectable by channel  
- **Rate limiting** shapes sent/sec under load  
- **Retries / DLQ** appear when failure injection is raised  
- **Dual auth** (JWT dashboard + API key send) matches production messaging platforms  

## Honest scope

This is an MVP/portfolio orchestrator — not a production CPaaS. Real carrier connectors, distributed limiters, multi-region failover, and billing are intentionally out of scope (see requirements). The point is to show how I design **dispatch backends** that hold up under volume and explain their trade-offs.

## Related docs

- [01 — Requirements](./01-requirements.md)
- [02 — System design](./02-system-design.md)
