# DispatchGrid — Phase 1: Requirements

## Vision

Multi-provider **campaign/dispatch orchestrator** that accepts high-volume send requests (SMS, email, webhook), applies **per-provider rate limits**, **retries with backoff**, routes exhausted failures to a **DLQ status**, records **delivery receipts**, and enforces **per-tenant quotas** — with simulated providers for portfolio demos.

## Target users

| Role | Goals |
|---|---|
| **Admin / Operator** | Create campaigns, run simulations, watch throughput, inject failures |
| **Machine client** | Authenticate with API key and enqueue sends |
| **Portfolio visitor** | Click simulate, see metrics move, read in-product architecture |

## Primary use cases

1. JWT login to ops dashboard  
2. Create campaign → add or generate recipients → start dispatch  
3. Outbox persists pending work; workers drain with provider strategy  
4. Token-bucket rate limiting per provider  
5. Failed sends retry with backoff; after max attempts → `FAILED_DLQ`  
6. Simulated delivery receipts update status to `DELIVERED` / `FAILED`  
7. Demo: simulate N messages for a channel; watch Throughput Console  
8. Failure injection (latency / fail %) per provider without redeploy  

## MVP scope (in)

- Demo tenant + `admin@dispatchgrid.demo` / `password`  
- JWT for dashboard; `X-Api-Key` for `/api/send`  
- Channels: SMS, EMAIL, WEBHOOK via fake providers  
- Outbox table + `@Async` (local) or RabbitMQ (docker)  
- H2 local / Postgres docker  
- Metrics: messages sent, sent/sec, p95, outbox depth, failures  
- Static dark industrial ops UI (slate/teal/amber, IBM Plex)  
- Unit tests: rate limiter + provider selection  
- CORS enabled; Dockerfile + Compose  

## Out of MVP (later)

- Real Twilio / SES / partner connectors  
- Distributed rate limiting (Redis)  
- Multi-region failover / weighted provider routing  
- Billing / SSO / audit export  
- Kafka event bus  

## Success criteria for portfolio

- Visitor can open demo, simulate 500 SMS, see sent/sec and campaigns complete  
- Case study can honestly claim: outbox, rate limits, retries/DLQ, dual auth, measurable throughput  
