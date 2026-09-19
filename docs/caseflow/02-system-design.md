# CaseFlow — Phase 2: System Design

## Architecture (modular monolith)

```
┌─────────────┐     REST/JWT      ┌──────────────────────────────────────┐
│  Web UI     │ ───────────────►  │  caseflow-api (Spring Boot)          │
│  (Next.js)  │ ◄── metrics SSE   │  adapters → application → domain     │
└─────────────┘                   └───────┬──────────┬─────────┬─────────┘
                                          │          │         │
                                     PostgreSQL   RabbitMQ   Redis*
```

\*Redis optional in MVP for assignment locks; Postgres optimistic locking is enough for demo scale.

### Packages

- `domain` — Case, Agent, Queue, RoutingRule, AuditEvent entities & policies  
- `application` — use cases (IngestEvent, AssignCase, TransitionStatus, SimulateLoad)  
- `adapters.web` — REST controllers, security, DTOs  
- `adapters.persistence` — JPA repositories  
- `adapters.messaging` — RabbitMQ publishers/consumers  
- `adapters.metrics` — ThroughputConsole metrics collector  

## Main domain entities

- **Tenant** — id, name  
- **User/Agent** — id, tenantId, email, role, skills[], active, lastAssignedAt  
- **Case** — id, tenantId, externalRef, title, priority, skills[], status, queueName, assigneeId, slaDeadline, createdAt  
- **RoutingRule** — id, tenantId, matchPriority?, matchSkill?, targetQueue, priorityWeight  
- **AuditEvent** — id, caseId, actorId, action, payloadJson, createdAt  
- **IngestEvent** — payload for pipeline  

### Case statuses

`NEW` → `QUEUED` → `ASSIGNED` → `IN_PROGRESS` → `RESOLVED` | `CLOSED`

## Database (PostgreSQL)

- `tenants`, `users`, `cases`, `routing_rules`, `audit_events`, `metrics_samples`  
- Indexes: `(tenant_id, status)`, `(tenant_id, queue_name, status)`, `(assignee_id, status)`  
- Optimistic lock `@Version` on `cases` for concurrent assignment  

## API (REST)

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/login` | JWT login |
| GET | `/api/cases` | List/filter cases |
| GET | `/api/cases/{id}` | Case detail + audit |
| POST | `/api/events` | Ingest event (creates case async) |
| POST | `/api/cases/{id}/transition` | Status change |
| POST | `/api/demo/simulate` | Enqueue N synthetic events |
| GET | `/api/metrics/throughput` | Console metrics |
| POST | `/api/demo/failure-injection` | Configure slow/fail % |
| GET | `/api/agents` | Agents + load |
| GET | `/api/queues` | Queue depths |
| GET | `/api/architecture` | Static ADR/architecture JSON for explorer |

## Messaging

- Exchange: `caseflow.events`  
- Queue: `caseflow.ingest` → consumer: persist case + evaluate rules → publish `caseflow.assign`  
- Queue: `caseflow.assign` → consumer: round-robin assign  
- DLQ: `caseflow.dlq`  

## Security

- Spring Security + JWT  
- Roles: ADMIN, SUPERVISOR, AGENT  
- Demo credentials seeded in Compose  

## Design decisions (ADRs)

1. **Modular monolith** — one deployable for portfolio; clear package boundaries  
2. **PostgreSQL** — transactional assignment + audit  
3. **RabbitMQ** — mirrors real queue/routing work; visible in demo  
4. **Round-robin** — simple, fair, explainable (CV-aligned)  
5. **No GraphQL** — REST + OpenAPI sufficient  

## Deployment

- `docker compose up` — local/demo  
- GitHub Actions: build + test  
- Cloud: single JVM service + managed Postgres/Rabbit or Compose on a VPS  

## Testing strategy

- Unit: routing matcher, round-robin selector  
- Integration: Testcontainers Postgres + Rabbit when practical; slice tests otherwise  
