package com.amrsamy.dispatchgrid.adapters.web;

import com.amrsamy.dispatchgrid.adapters.provider.ProviderRegistry;
import com.amrsamy.dispatchgrid.adapters.web.dto.ApiDtos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/architecture")
public class ArchitectureController {

    private final Environment environment;
    private final ProviderRegistry providerRegistry;

    public ArchitectureController(Environment environment, ProviderRegistry providerRegistry) {
        this.environment = environment;
        this.providerRegistry = providerRegistry;
    }

    @GetMapping
    public ApiDtos.ArchitectureResponse architecture(
            @Value("${dispatchgrid.dispatch.max-attempts:3}") int maxAttempts) {
        String profile = String.join(",", environment.getActiveProfiles());
        if (profile.isBlank()) {
            profile = "default";
        }
        return new ApiDtos.ArchitectureResponse(
                "DispatchGrid",
                List.of(
                        Map.of("name", "Campaign API", "role", "Create campaigns, recipients, start dispatch"),
                        Map.of("name", "Outbox", "role", "Transactional pending send queue (outbox_messages)"),
                        Map.of("name", "Dispatch pipeline", "role", "@Async (local) or RabbitMQ (docker)"),
                        Map.of("name", "Provider adapters", "role", "FakeSms / FakeEmail / FakeWebhook strategy"),
                        Map.of("name", "Rate limiters", "role", "Per-provider in-memory token buckets"),
                        Map.of("name", "Receipts + DLQ", "role", "Simulated delivery callbacks; FAILED_DLQ after retries"),
                        Map.of("name", "Ops dashboard", "role", "JWT login, metrics, failure injection")),
                List.of(
                        Map.of(
                                "id", "ADR-001",
                                "title", "Outbox for dispatch",
                                "decision", "Persist outbox_messages in the same DB transaction as message rows, then async/Rabbit workers drain them.",
                                "why", "Avoids lost sends if the process crashes after commit; mirrors high-volume messaging reliability patterns."),
                        Map.of(
                                "id", "ADR-002",
                                "title", "Token-bucket rate limits",
                                "decision", "In-memory TokenBucketRateLimiter per fake provider for local demos.",
                                "why", "Shows provider throttling without Redis; swap for distributed limiter in production."),
                        Map.of(
                                "id", "ADR-003",
                                "title", "Dual auth",
                                "decision", "JWT for dashboard humans; X-Api-Key for machine send API.",
                                "why", "Matches real messaging platforms where ops UI and send pipelines use different credentials."),
                        Map.of(
                                "id", "ADR-004",
                                "title", "Profile-split messaging",
                                "decision", "local=@Async+H2; docker=Postgres+RabbitMQ.",
                                "why", "Portfolio visitors can run without Docker; Compose still demonstrates AMQP.")),
                Map.of(
                        "activeProfiles", profile,
                        "maxAttempts", maxAttempts,
                        "providers", providerRegistry.providerKeysByChannel()));
    }
}
