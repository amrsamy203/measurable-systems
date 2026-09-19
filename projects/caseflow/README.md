# CaseFlow

Multi-tenant investigation/ops case engine: **ingest → rule evaluation → priority queues → round-robin assignment → SLA → audit trail**, with a live **Throughput Console**.

Portfolio demo for a Java backend engineer — measurable routing and throughput, not just CRUD.

## Stack

- Java 21, Spring Boot 3.3.x, Maven
- Spring Web, Data JPA, Security (JWT), AMQP
- **local** profile: H2 + `@Async` in-process pipeline (no Docker required)
- **docker** profile: PostgreSQL + RabbitMQ

## Quick start (local)

```bash
cd projects/caseflow
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080)

| User | Password | Role |
|------|----------|------|
| `admin@caseflow.demo` | `password` | ADMIN |
| `supervisor@caseflow.demo` | `password` | SUPERVISOR |
| `agent1@caseflow.demo` | `password` | AGENT |

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Docker Compose

```bash
docker compose up --build
```

- App: http://localhost:8080  
- RabbitMQ management: http://localhost:15672 (`caseflow` / `caseflow`)

## Demo flow

1. Sign in as admin  
2. Click **Simulate 100 events**  
3. Watch Throughput Console (events/sec, p95, queue depth)  
4. Cases appear on the board with queue + assignee  
5. Optionally set failure injection (slow ms / fail %)  

## API (selected)

| Method | Path | Notes |
|--------|------|-------|
| POST | `/api/auth/login` | JWT |
| POST | `/api/events` | Ingest |
| POST | `/api/demo/simulate` | `{ "count": 100 }` |
| GET | `/api/cases`, `/api/cases/{id}` | List / detail + audit |
| POST | `/api/cases/{id}/transition` | `{ "status": "IN_PROGRESS" }` |
| GET | `/api/agents`, `/api/queues` | Load + depths |
| GET | `/api/metrics/throughput` | Console metrics |
| POST | `/api/demo/failure-injection` | Demo chaos knobs |
| GET | `/api/architecture` | Components + ADRs |

## Profiles

| Profile | DB | Messaging |
|---------|----|-----------|
| `local` (default) | H2 in-memory | `@Async` pipeline |
| `docker` | PostgreSQL | RabbitMQ (`caseflow.ingest` → `caseflow.assign`) |

JWT secret: env `CASEFLOW_JWT_SECRET` (demo default in `application.yml`).

## Tests

```bash
mvn test
```

Unit tests cover `RoutingMatcher` and `RoundRobinSelector`.

## Package layout

```
com.amrsamy.caseflow
  domain/          # CaseStatus, Priority, RoutingMatcher, RoundRobinSelector
  application/     # use cases + ports
  adapters.web/    # REST + JWT security + static ops UI
  adapters.persistence/
  adapters.messaging/  # local async | RabbitMQ
  adapters.metrics/    # Throughput Console collector
  config/
```

## License

Demo / portfolio code — use freely with attribution appreciated.
