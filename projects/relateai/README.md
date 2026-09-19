# RelateAI

Community / SaaS backend: **social + topic graph**, **daily AI challenges**, and **media upload** — with OpenAI when available and a deterministic mock otherwise.

Portfolio demo for graph-aware Spring Boot backends without requiring Neo4j.

## Stack

- Java 21, Spring Boot 3.3.x, Maven
- Spring Web, Data JPA, Security (JWT), Validation, Actuator
- **local** profile: H2 in-memory + `./data/uploads`
- **docker** profile: PostgreSQL + upload volume
- OpenAI via `OPENAI_API_KEY` (optional) → falls back to `MockAiClient`

## Quick start (local)

```bash
cd projects/relateai
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080)

| User | Password | Role |
|------|----------|------|
| `user1@relateai.demo` | `password` | USER |
| `admin@relateai.demo` | `password` | ADMIN |

Also seeded: `user2@`, `user3@`, `user4@relateai.demo` / `password`.

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Optional real AI:

```bash
# PowerShell
$env:OPENAI_API_KEY="sk-..."
mvn spring-boot:run
```

## Docker Compose

```bash
docker compose up --build
```

- App: http://localhost:8080  
- Postgres host port: `5433` (avoids clashing with other demos)

Pass OpenAI key through the environment if desired:

```bash
OPENAI_API_KEY=sk-... docker compose up --build
```

## Demo flow

1. Sign in as `user1@relateai.demo`
2. Read today’s challenge (generated via Mock or OpenAI)
3. Submit a response
4. Refresh **Graph insights** (top connected, hottest topics, recommendations)
5. Upload a small image and open the returned `/uploads/...` URL
6. Watch **Throughput** counters move

## API (selected)

| Method | Path | Notes |
|--------|------|-------|
| POST | `/api/auth/login` | JWT |
| GET | `/api/users` | List users |
| POST / DELETE | `/api/users/{id}/follow` | Follow / unfollow |
| GET | `/api/topics` | List topics |
| POST | `/api/topics/{id}/join` | Join topic |
| POST | `/api/interactions` | LIKE / COMMENT |
| GET | `/api/graph/top-connected` | Degree ranking |
| GET | `/api/graph/hottest-topics` | Topic activity |
| GET | `/api/graph/recommendations` | Shared topics |
| GET | `/api/challenges/today` | Get or generate |
| POST | `/api/challenges/{id}/responses` | Submit answer |
| POST | `/api/media/upload` | Multipart image |
| GET | `/api/metrics/throughput` | Demo metrics |
| GET | `/api/architecture` | Components + ADRs |

## Profiles

| Profile | DB | Storage | AI |
|---------|----|---------|----|
| `local` (default) | H2 mem | `./data/uploads` | Mock unless `OPENAI_API_KEY` set |
| `docker` | PostgreSQL | `/app/data/uploads` | Same |

## Tests

```bash
mvn test
```

Unit tests cover `GraphInsightService` (top connected, hottest topics, shared-topic recommendations).

## Package layout

```
com.amrsamy.relateai
  domain/          # Role, InteractionType, GraphInsightService
  application/     # ports + Challenge / SocialGraph / Media services
  adapters.web/    # REST + JWT + static dashboard
  adapters.persistence/
  adapters.ai/     # OpenAiClient + MockAiClient
  adapters.storage/# LocalBlobStorageAdapter
  adapters.metrics/
  config/
```

## Docs

- [Requirements](../../docs/relateai/01-requirements.md)
- [System design](../../docs/relateai/02-system-design.md)
- [Case study](../../docs/relateai/03-case-study.md)

## License

Demo / portfolio code — use freely with attribution appreciated.
