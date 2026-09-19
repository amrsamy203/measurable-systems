# RelateAI — Phase 1: Requirements

## Vision

Community / SaaS backend that models a **social + topic graph**, serves **daily AI challenge prompts**, and supports **media uploads** — demonstrating OpenAI-backed features and practical relationship/graph analysis without Neo4j.

## Target users

| Role | Goals |
|---|---|
| **Member** | Follow users, join topics, respond to daily challenges, upload images |
| **Admin** | Seed/manage demo data; inspect metrics and architecture |
| **Portfolio visitor** | Sign in, explore graph insights, generate/view challenges, upload media |

## Primary use cases

1. Authenticate with JWT (seeded demo users)
2. Follow / unfollow other users
3. Browse and join topics; record interaction events (like, comment, challenge_response)
4. Query graph insights: top connected users, hottest topics, recommended users by shared topics
5. Generate (or fetch) today’s daily challenge via OpenAI or deterministic mock AI
6. Submit challenge responses
7. Upload an image to local blob storage; receive a URL path
8. View throughput metrics and in-product architecture

## MVP scope (in)

- Java 21 / Spring Boot 3.3 modular monolith
- JWT auth + roles (USER, ADMIN)
- Seeded users: `user1@relateai.demo`, `admin@relateai.demo` / `password`
- Graph-style modeling in PostgreSQL / H2: users, follows, topics, user–topic membership, interactions
- Daily challenge generation: OpenAI when `OPENAI_API_KEY` is set; otherwise `MockAiClient`
- Local file storage under `./data/uploads` behind `BlobStoragePort` (`LocalBlobStorageAdapter`)
- REST API + static dark industrial dashboard
- Metrics: challenges generated, interactions recorded, uploads, follows
- Profiles: `local` (H2) and `docker` (Postgres)
- Dockerfile + docker-compose
- Unit tests for graph recommendation / insight logic

## Out of MVP (later)

- Real Neo4j / graph DB
- Push notifications, realtime chat
- Production S3/Azure Blob adapters (interface ready)
- Billing / multi-tenant SaaS
- Mobile clients
- Content moderation pipelines

## Success criteria for portfolio

- Demo works without an OpenAI key (mock AI)
- Graph APIs return explainable rankings from relational data
- Visitor can log in, see challenges + insights, upload an image, and watch metrics move
