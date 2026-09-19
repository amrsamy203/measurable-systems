package com.amrsamy.relateai.adapters.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/architecture")
public class ArchitectureController {

    @GetMapping
    public Map<String, Object> architecture() {
        return Map.of(
                "system", "RelateAI",
                "style", "modular-monolith",
                "stack", List.of("Java 21", "Spring Boot 3.3", "JPA", "JWT", "H2/PostgreSQL"),
                "components", List.of(
                        Map.of("name", "auth", "desc", "JWT login with seeded demo users"),
                        Map.of("name", "social-graph", "desc", "Follows + topic memberships + interactions"),
                        Map.of("name", "graph-insights", "desc", "Top connected, hottest topics, shared-topic recommendations"),
                        Map.of("name", "daily-challenge", "desc", "OpenAI or MockAiClient prompt generation"),
                        Map.of("name", "media", "desc", "BlobStoragePort with LocalBlobStorageAdapter"),
                        Map.of("name", "metrics", "desc", "In-memory throughput counters for demos")
                ),
                "adrs", List.of(
                        Map.of("id", "ADR-1", "decision", "SQL graph-style modeling", "why", "Practical joins over Neo4j for MVP demos"),
                        Map.of("id", "ADR-2", "decision", "AI port with mock fallback", "why", "Demos work without OPENAI_API_KEY"),
                        Map.of("id", "ADR-3", "decision", "Blob storage port", "why", "Swap local FS for S3/Azure later"),
                        Map.of("id", "ADR-4", "decision", "Modular monolith", "why", "Clear packages, one deployable")
                ),
                "profiles", Map.of(
                        "local", "H2 in-memory + local uploads",
                        "docker", "PostgreSQL + local uploads volume"
                )
        );
    }
}
