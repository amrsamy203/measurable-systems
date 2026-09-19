# RelateAI — Phase 2: System Design

## Architecture (modular monolith)

```
┌──────────────┐     REST/JWT      ┌──────────────────────────────────────────┐
│  Static UI   │ ───────────────►  │  relateai-api (Spring Boot)               │
│  (dark UI)   │ ◄── JSON          │  adapters → application → domain         │
└──────────────┘                   └──────┬──────────┬──────────┬─────────────┘
                                          │          │          │
                                     PostgreSQL   OpenAI*   Local blob
                                     / H2 local   / Mock    ./data/uploads
```

\*OpenAI used only when `OPENAI_API_KEY` is present; otherwise deterministic `MockAiClient`.

### Packages

- `domain` — Role, InteractionType, GraphInsightService (pure ranking logic)
- `application` — use cases + ports (`AiClientPort`, `BlobStoragePort`)
- `adapters.web` — REST, JWT security, DTOs, static dashboard
- `adapters.persistence` — JPA entities & repositories
- `adapters.ai` — OpenAI + Mock clients
- `adapters.storage` — LocalBlobStorageAdapter
- `adapters.metrics` — in-memory throughput counters
- `config` — Security, seeder, AI bean wiring

## Main domain entities

| Entity | Purpose |
|--------|---------|
| **User** | email, displayName, role, passwordHash |
| **Follow** | followerId → followeeId (directed edge) |
| **Topic** | slug, name, description |
| **UserTopic** | user membership in topic |
| **Interaction** | actor, optional target user/topic, type, payload |
| **Challenge** | date, prompt text, source (OPENAI \| MOCK) |
| **ChallengeResponse** | user response to a challenge |
| **MediaAsset** | storage key, content type, public path, uploader |

### Interaction types

`LIKE` | `COMMENT` | `CHALLENGE_RESPONSE` | `FOLLOW` | `JOIN_TOPIC`

## Graph modeling (relational)

Instead of Neo4j, edges live in tables:

- `follows(follower_id, followee_id)` — social graph
- `user_topics(user_id, topic_id)` — bipartite interest graph
- `interactions(...)` — weighted activity for “hottest topic” scoring

Insight algorithms (unit-tested in `GraphInsightService`):

1. **Top connected** — degree = followers + following
2. **Hottest topics** — interaction count per topic (with JOIN_TOPIC / LIKE / COMMENT weight)
3. **Recommendations** — users sharing the most topics who are not already followed

## API (REST)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/login` | JWT login |
| GET | `/api/users` | List users |
| POST | `/api/users/{id}/follow` | Follow user |
| DELETE | `/api/users/{id}/follow` | Unfollow |
| GET | `/api/topics` | List topics |
| POST | `/api/topics/{id}/join` | Join topic |
| POST | `/api/interactions` | Record interaction |
| GET | `/api/graph/top-connected` | Top connected users |
| GET | `/api/graph/hottest-topics` | Hottest topics |
| GET | `/api/graph/recommendations` | Shared-topic recommendations |
| GET | `/api/challenges/today` | Get or generate today’s challenge |
| POST | `/api/challenges/{id}/responses` | Submit response |
| GET | `/api/challenges/{id}/responses` | List responses |
| POST | `/api/media/upload` | Multipart image upload |
| GET | `/api/metrics/throughput` | Demo metrics |
| GET | `/api/architecture` | Components + ADRs JSON |

## Security

- Spring Security + JWT (stateless)
- Roles: `USER`, `ADMIN`
- Public: static UI, `/api/auth/**`, `/api/architecture`, swagger, actuator health
- Uploaded files served under `/uploads/**` (public for demo)

## Design decisions (ADRs)

1. **Modular monolith** — one deployable; clear ports for AI and blob storage
2. **PostgreSQL graph-style** — practical joins over Neo4j for MVP demos
3. **Mock AI fallback** — demos always work without cloud keys
4. **Blob port + local adapter** — swap to S3/Azure later without rewriting controllers
5. **REST + OpenAPI** — sufficient for dashboard and portfolio reviewers

## Deployment

- `mvn spring-boot:run` — local H2 + mock/real OpenAI
- `docker compose up --build` — app + Postgres

## Testing strategy

- Unit: `GraphInsightService` recommendation and ranking
- Manual: dashboard login → challenge → upload → metrics
