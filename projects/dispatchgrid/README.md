# DispatchGrid

Multi-provider **campaign/dispatch orchestrator** (SMS / email / webhook adapters) with **token-bucket rate limits**, **retries + backoff**, **outbox**, **FAILED_DLQ**, **delivery receipts**, and **per-tenant quotas** — plus a live Throughput Console.

Portfolio demo aligned with high-volume messaging / messaging-platform backend work.

## Stack

- Java 21, Spring Boot 3.3.x, Maven
- Spring Web, Data JPA, Security (JWT + API keys), AMQP
- **local** profile: H2 + `@Async` outbox drain (no Docker required)
- **docker** profile: PostgreSQL + RabbitMQ

## Quick start (local)

```bash
cd projects/dispatchgrid
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080)

| Credential | Value |
|------------|-------|
| Dashboard login | `admin@dispatchgrid.demo` / `password` |
| Machine API key | `dg_demo_sk_live_7f3a9c2e8b1d4f6a` (header `X-Api-Key`) |

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Docker Compose

```bash
docker compose up --build
```

- App: http://localhost:8080  
- RabbitMQ management: http://localhost:15672 (`dispatchgrid` / `dispatchgrid`)

## Demo flow

1. Sign in as admin  
2. Set channel + count → **Simulate campaign** (e.g. SMS × 500)  
3. Watch Throughput Console (sent/sec, p95, outbox depth, failures)  
4. Campaigns table updates as status moves to RUNNING → COMPLETED  
5. Optionally inject provider latency / fail %  

## API (selected)

| Method | Path | Auth | Notes |
|--------|------|------|-------|
| POST | `/api/auth/login` | — | JWT |
| POST | `/api/campaigns` | JWT | Create campaign |
| POST | `/api/campaigns/{id}/recipients` | JWT | Add destinations |
| POST | `/api/campaigns/{id}/recipients/generate` | JWT | Synthetic recipients |
| POST | `/api/campaigns/{id}/start` | JWT | Write outbox + start dispatch |
| POST | `/api/send` | `X-Api-Key` | Machine single-send |
| POST | `/api/demo/simulate-campaign` | JWT | `{ "channel": "SMS", "count": 500 }` |
| GET | `/api/metrics/throughput` | JWT | sent/sec, p95, queue depth, failures |
| POST | `/api/demo/failure-injection` | JWT | Provider chaos knobs |
| GET | `/api/architecture` | public | Components + ADRs |

### Machine send example

```bash
curl -X POST http://localhost:8080/api/send \
  -H "Content-Type: application/json" \
  -H "X-Api-Key: dg_demo_sk_live_7f3a9c2e8b1d4f6a" \
  -d '{"channel":"SMS","destination":"+15551234567","body":"Hello from DispatchGrid"}'
```

## Profiles

| Profile | DB | Messaging |
|---------|----|-----------|
| `local` (default) | H2 in-memory | `@Async` + outbox poller |
| `docker` | PostgreSQL | RabbitMQ (`dispatchgrid.dispatch`) |

JWT secret: env `DISPATCHGRID_JWT_SECRET` (demo default in `application.yml`).

## Tests

```bash
mvn test
```

Unit tests cover `TokenBucketRateLimiter` and `ProviderSelector`.

## Package layout

```
com.amrsamy.dispatchgrid
  domain/            # Channel, statuses, TokenBucketRateLimiter, ProviderSelector
  application/       # Campaign + dispatch pipeline use cases
  adapters.web/      # REST, JWT + API key security, static ops UI
  adapters.persistence/
  adapters.provider/ # FakeSms / FakeEmail / FakeWebhook
  adapters.messaging/# local async | RabbitMQ
  adapters.metrics/  # Throughput collector + failure injection
  config/
```

## License

Demo / portfolio code — use freely with attribution appreciated.
