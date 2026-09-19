# CaseFlow — Phase 1: Requirements

## Vision

Multi-tenant investigation/ops case engine that ingests events, evaluates routing rules, assigns work fairly across agents (round-robin / skill-based), tracks SLAs, and keeps a full audit trail — with a live Throughput Console for portfolio demos.

## Target users

| Role | Goals |
|---|---|
| **Admin** | Manage tenants, agents, routing rules; run load simulations |
| **Supervisor** | Monitor queues, reassign cases, watch SLA breaches |
| **Agent** | Claim/work assigned cases; update status |
| **Portfolio visitor** | Run demo scenarios; see metrics and architecture |

## Primary use cases

1. Ingest an event → create or update a case  
2. Evaluate routing rules (priority, skill tags, queue)  
3. Assign case to next eligible agent (round-robin)  
4. Agent transitions case status (OPEN → IN_PROGRESS → RESOLVED)  
5. Supervisor views board + queue depths  
6. Demo user triggers “simulate N events” and watches Throughput Console  
7. Audit log records every meaningful state change  

## MVP scope (in)

- Single demo tenant pre-seeded + ability to register login users with roles  
- JWT auth + RBAC (ADMIN, SUPERVISOR, AGENT)  
- Cases with priority, skill tags, status, SLA deadline  
- Routing rules (match on priority/skill → queue name)  
- Round-robin assignment per queue  
- RabbitMQ async processing for ingest → route → assign pipeline  
- PostgreSQL persistence + audit events  
- REST API + OpenAPI  
- Metrics endpoint (events/sec, p95 latency, queue depth, failures)  
- Thin Next.js (or simple React) ops board + Throughput Console  
- Docker Compose (app, Postgres, RabbitMQ, Redis optional)  
- Failure injection toggle (slow processing / reject %) for demos  

## Out of MVP (later)

- Multi-tenant billing / SSO  
- Full skill-based ML matching  
- GraphQL  
- Kafka  
- Mobile apps  
- Real AML/compliance rule engines or proprietary scenarios  

## Success criteria for portfolio

- Visitor can open demo, click simulate, see cases appear and metrics move  
- Case study can honestly claim: queue-driven routing, fair assignment, measurable throughput  
