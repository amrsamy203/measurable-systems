# RelateAI — Case Study

## Problem

Community products need more than CRUD users: they need a **relationship graph** (who follows whom, who shares interests), **timely engagement loops** (daily prompts), and **media**. Many demos either fake AI or bolt on Neo4j prematurely. RelateAI shows a practical Spring Boot approach: model the graph in SQL, plug in OpenAI when available, and keep demos reliable with a mock AI client.

## What I built

A Spring Boot 3 / Java 21 MVP that:

1. Authenticates members with **JWT** and seeds demo accounts
2. Models a **social + topic graph** in PostgreSQL/H2 (follows, memberships, interactions)
3. Exposes **graph insight APIs** — top connected users, hottest topics, shared-topic recommendations
4. Generates a **daily challenge** via OpenAI or a deterministic mock
5. Accepts **challenge responses** and **image uploads** through a storage port (`LocalBlobStorageAdapter`)
6. Serves a **dark industrial dashboard** plus `/api/metrics/throughput` and `/api/architecture`

### Architecture choices (ADRs)

| ADR | Decision | Why |
|-----|----------|-----|
| Modular monolith | Hexagonal-ish packages | Portfolio clarity without microservices sprawl |
| SQL graph modeling | Follows + user_topics + interactions | Enough for ranking demos; no Neo4j ops tax |
| AI port | OpenAI if key present else Mock | Demos never block on credentials |
| Blob port | Local filesystem MVP | Interface ready for S3/Azure |
| REST + static UI | No SPA framework | Fast to run and review |

### Daily challenge flow

```
GET /api/challenges/today
  → if challenge for today exists → return it
  → else AiClientPort.generateDailyChallenge()
       → OpenAiClient (OPENAI_API_KEY) or MockAiClient
  → persist Challenge(source=OPENAI|MOCK)
  → increment metrics.challengesGenerated
```

### Graph recommendation sketch

```
for candidate users not followed by me:
  score = | my_topics ∩ candidate_topics |
rank by score desc, return top N
```

## How to evaluate it

```bash
cd projects/relateai
mvn spring-boot:run
# open http://localhost:8080
# user1@relateai.demo / password
```

Or: `docker compose up --build` for Postgres-backed docker profile.

Optional: set `OPENAI_API_KEY` to use real prompts; omit it to use mock AI.

## Outcomes this demo is designed to show

- **Graph insights** computed from relational edges, not a dedicated graph DB
- **AI integration** with a graceful offline/mock path
- **Storage abstraction** for media uploads
- **Measurable activity** via throughput metrics
- **In-product architecture** documentation at `/api/architecture`

## Honest scope

This is an MVP/portfolio community backend — not a production social network. Neo4j, realtime feeds, moderation, and cloud blob adapters are intentionally out of scope. The point is to show how I design **graph-aware SaaS backends** with clean ports and demo-friendly defaults.

## Related docs

- [01 — Requirements](./01-requirements.md)
- [02 — System design](./02-system-design.md)
