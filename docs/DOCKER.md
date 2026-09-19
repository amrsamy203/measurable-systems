# Docker — full stack

Run everything from the repo root:

```powershell
# Ensure Docker Desktop is running, then:
$env:Path = "C:\Users\amrsa\AppData\Local\Programs\DockerDesktop\resources\bin;" + $env:Path
cd "c:\Freelance portofoli and projects"
docker compose up --build -d
```

## URLs

| Service | URL | Login |
|---|---|---|
| Portfolio | http://localhost:3000 | — |
| CaseFlow | http://localhost:8081 | admin@caseflow.demo / password |
| DispatchGrid | http://localhost:8082 | admin@dispatchgrid.demo / password |
| RelateAI | http://localhost:8083 | user1@relateai.demo / password |
| CaseFlow RabbitMQ UI | http://localhost:15672 | caseflow / caseflow |
| DispatchGrid RabbitMQ UI | http://localhost:15673 | dispatchgrid / dispatchgrid |

## Useful commands

```powershell
docker compose ps
docker compose logs -f caseflow
docker compose down          # stop
docker compose down -v       # stop + wipe DB volumes
```

## Audit fixes applied before first run

- DispatchGrid `OutboxPoller` enabled for `docker` profile (was `local` only)
- `.dockerignore` on all apps
- Portfolio nginx Dockerfile (static `output: "export"`)
- Root `docker-compose.yml` with non-colliding ports
- App Dockerfiles: curl + healthchecks, jar copy without `.original`
