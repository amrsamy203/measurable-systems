# Projects

Three Spring Boot demos under the **Measurable Systems** theme.

| Folder | One-liner | Port (Docker) |
|---|---|---|
| [caseflow](caseflow/) | Fair case routing with live throughput metrics | 8081 |
| [dispatchgrid](dispatchgrid/) | Multi-provider campaign dispatch with rate limits & DLQ | 8082 |
| [relateai](relateai/) | Social/topic graph + daily AI challenges + uploads | 8083 |

Prefer the root [`docker-compose.yml`](../docker-compose.yml) so all demos run together without port clashes.

Each project README covers:

- Business problem & MVP scope  
- Local `mvn spring-boot:run`  
- Standalone `docker compose up`  
- Demo users / API keys  
