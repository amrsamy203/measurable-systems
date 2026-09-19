# DispatchGrid — Phase 2: System Design

## Architecture (modular monolith)

```
┌─────────────┐  JWT dashboard   ┌──────────────────────────────────────┐
│  Ops UI     │ ───────────────► │  dispatchgrid-api (Spring Boot)      │
│  (static)   │ ◄── metrics poll │  adapters → application → domain     │
└─────────────┘                  └───────┬──────────┬─────────┬─────────┘
                                         │          │         │
                                    Postgres/H2  Outbox    Providers
                                         │          │      (fake SMS/
                                    RabbitMQ*    Rate      email/webhook)
                                                 buckets
```

\*RabbitMQ on `docker` profile only; `local` uses `@Async` + scheduled outbox poller.

### Packages

- `domain` — Channel, statuses, `TokenBucketRateLimiter`, `ProviderSelector`  
- `application` — campaign lifecycle, dispatch pipeline, demo simulation  
- `adapters.web` — REST, JWT + API-key filters, static UI  
- `adapters.persistence` — JPA entities / repositories  
- `adapters.provider` — FakeSms / FakeEmail / FakeWebhook  
- `adapters.messaging` — local async | RabbitMQ  
- `adapters.metrics` — Throughput Console + failure injection  

## Main domain entities

- **Tenant** — dailyQuota, usedToday  
- **User** — email, role (ADMIN / OPERATOR), password hash  
- **ApiKey** — machine credential for send API  
- **Campaign** — channel, template, status, counters  
- **DispatchMessage** — destination, status, attempts, provider ids  
- **OutboxMessage** — pending work linked to message id  
- **DeliveryReceipt** — simulated callback status  

### Message statuses

`PENDING` → `QUEUED` → `SENDING` → `SENT` → `DELIVERED` | `FAILED`  
Exhausted retries → `FAILED_DLQ`

### Campaign statuses

`DRAFT` → `RUNNING` → `COMPLETED` | `FAILED` | `CANCELLED`

## Pipeline

```
start campaign / simulate
  → persist messages (PENDING) + outbox_messages (PENDING)  [same TX]
  → enqueue (async or RabbitMQ)
  → claim token-bucket → provider.send()
  → success: SENT + receipt (DELIVERED/FAILED) + metrics
  → failure: backoff retry outbox; after maxAttempts → FAILED_DLQ
```

## API (REST)

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/login` | JWT login |
| POST/GET | `/api/campaigns…` | Campaign CRUD-ish + start |
| POST | `/api/send` | Machine send (`X-Api-Key`) |
| POST | `/api/demo/simulate-campaign` | Synthetic load |
| GET | `/api/metrics/throughput` | Console metrics |
| POST | `/api/demo/failure-injection` | Chaos knobs |
| GET | `/api/architecture` | Components + ADRs |

## Security

- Spring Security, stateless  
- JWT Bearer for dashboard APIs  
- `X-Api-Key` for machine send  
- CORS open for local demos  

## Design decisions (ADRs)

1. **Outbox** — durable pending sends in DB before async work  
2. **Token-bucket in-memory** — explainable provider throttling for demos  
3. **Dual auth** — humans vs machines  
4. **Profile-split messaging** — run without Docker; Compose shows AMQP  
5. **Strategy providers** — swap fakes for real adapters later  

## Deployment

- `mvn spring-boot:run` — local  
- `docker compose up --build` — Postgres + RabbitMQ + app  

## Testing strategy

- Unit: `TokenBucketRateLimiter`, `ProviderSelector`  
- Manual: simulate campaign + failure injection on dashboard  
