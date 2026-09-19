package com.amrsamy.caseflow.adapters.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ArchitectureController {

    @GetMapping("/architecture")
    public Map<String, Object> architecture() {
        return Map.of(
                "name", "CaseFlow",
                "style", "Modular monolith (hexagonal packages)",
                "components", List.of(
                        Map.of(
                                "id", "web-ui",
                                "name", "Ops Dashboard",
                                "type", "Static SPA",
                                "description", "Login, cases board, Throughput Console, failure injection, architecture explorer"),
                        Map.of(
                                "id", "api",
                                "name", "caseflow-api",
                                "type", "Spring Boot",
                                "description", "REST + JWT; application services orchestrate ingest, route, assign, transition"),
                        Map.of(
                                "id", "domain",
                                "name", "Domain policies",
                                "type", "Pure Java",
                                "description", "RoutingMatcher + RoundRobinSelector — fair, explainable assignment"),
                        Map.of(
                                "id", "pipeline",
                                "name", "Ingest pipeline",
                                "type", "Async / AMQP",
                                "description", "local: @Async in-process; docker: RabbitMQ ingest → assign with DLQ"),
                        Map.of(
                                "id", "db",
                                "name", "PostgreSQL / H2",
                                "type", "Persistence",
                                "description", "Cases, agents, routing rules, audit events; optimistic locking on cases"),
                        Map.of(
                                "id", "metrics",
                                "name", "Throughput Console",
                                "type", "In-process collector",
                                "description", "eventsProcessed, events/sec, p95 latency, queue depth, failures")
                ),
                "adrs", List.of(
                        Map.of(
                                "id", "ADR-001",
                                "title", "Modular monolith",
                                "status", "Accepted",
                                "decision", "Single deployable with clear package boundaries for a portfolio demo that still mirrors production structure."),
                        Map.of(
                                "id", "ADR-002",
                                "title", "PostgreSQL for assignment + audit",
                                "status", "Accepted",
                                "decision", "Transactional writes and optimistic locking are enough at demo scale; Redis locks deferred."),
                        Map.of(
                                "id", "ADR-003",
                                "title", "RabbitMQ for docker profile",
                                "status", "Accepted",
                                "decision", "Real queue/routing visibility in Compose; local profile uses @Async so demos run without Docker."),
                        Map.of(
                                "id", "ADR-004",
                                "title", "Round-robin per queue skills",
                                "status", "Accepted",
                                "decision", "Oldest lastAssignedAt wins among skill-eligible agents — fair and CV-aligned."),
                        Map.of(
                                "id", "ADR-005",
                                "title", "REST + OpenAPI, not GraphQL",
                                "status", "Accepted",
                                "decision", "Sufficient for ops board and metrics; keeps surface area small.")
                ),
                "flows", List.of(
                        "POST /api/events → persist NEW case → enqueue pipeline",
                        "Route: evaluate RoutingRules → set queue + SLA → QUEUED",
                        "Assign: RoundRobinSelector → ASSIGNED + audit",
                        "Agent: POST /api/cases/{id}/transition → IN_PROGRESS → RESOLVED"
                )
        );
    }
}
