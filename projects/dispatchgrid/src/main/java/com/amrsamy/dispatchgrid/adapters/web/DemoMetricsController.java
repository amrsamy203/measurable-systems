package com.amrsamy.dispatchgrid.adapters.web;

import com.amrsamy.dispatchgrid.adapters.metrics.FailureInjectionState;
import com.amrsamy.dispatchgrid.adapters.metrics.ThroughputMetricsCollector;
import com.amrsamy.dispatchgrid.adapters.persistence.entity.CampaignEntity;
import com.amrsamy.dispatchgrid.adapters.persistence.repo.OutboxMessageRepository;
import com.amrsamy.dispatchgrid.adapters.web.dto.ApiDtos;
import com.amrsamy.dispatchgrid.adapters.web.security.DispatchGridUserPrincipal;
import com.amrsamy.dispatchgrid.application.service.DemoSimulationService;
import com.amrsamy.dispatchgrid.config.DataSeeder;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DemoMetricsController {

    private final DemoSimulationService demoSimulationService;
    private final ThroughputMetricsCollector metricsCollector;
    private final OutboxMessageRepository outboxMessageRepository;
    private final FailureInjectionState failureInjectionState;

    public DemoMetricsController(
            DemoSimulationService demoSimulationService,
            ThroughputMetricsCollector metricsCollector,
            OutboxMessageRepository outboxMessageRepository,
            FailureInjectionState failureInjectionState) {
        this.demoSimulationService = demoSimulationService;
        this.metricsCollector = metricsCollector;
        this.outboxMessageRepository = outboxMessageRepository;
        this.failureInjectionState = failureInjectionState;
    }

    @PostMapping("/demo/simulate-campaign")
    public ApiDtos.CampaignResponse simulate(
            Authentication authentication,
            @Valid @RequestBody ApiDtos.SimulateCampaignRequest request) {
        DispatchGridUserPrincipal user = (DispatchGridUserPrincipal) authentication.getPrincipal();
        CampaignEntity campaign = demoSimulationService.simulateCampaign(
                user.getTenantId(), request.channel(), request.count());
        return new ApiDtos.CampaignResponse(
                campaign.getId(),
                campaign.getName(),
                campaign.getChannel(),
                campaign.getStatus(),
                campaign.getRecipientCount(),
                campaign.getSentCount(),
                campaign.getFailedCount(),
                campaign.getCreatedAt(),
                campaign.getStartedAt(),
                campaign.getCompletedAt());
    }

    @GetMapping("/metrics/throughput")
    public ApiDtos.ThroughputResponse throughput() {
        long depth = outboxMessageRepository.countPending();
        ThroughputMetricsCollector.ThroughputSnapshot snap = metricsCollector.snapshot(depth);
        return new ApiDtos.ThroughputResponse(
                snap.messagesSent(),
                snap.sentPerSecond(),
                snap.p95LatencyMs(),
                snap.queueDepth(),
                snap.failureCount(),
                snap.lastUpdated());
    }

    @PostMapping("/demo/failure-injection")
    public Map<String, Object> failureInjection(@Valid @RequestBody ApiDtos.FailureInjectionRequest request) {
        String key = request.providerKey() == null || request.providerKey().isBlank()
                ? FailureInjectionState.GLOBAL
                : request.providerKey();
        failureInjectionState.configure(key, request.slowMs(), request.failPercent());
        return Map.of(
                "applied", true,
                "providerKey", key,
                "slowMs", request.slowMs(),
                "failPercent", request.failPercent(),
                "state", failureInjectionState.snapshot());
    }

    @GetMapping("/demo/info")
    public Map<String, Object> demoInfo() {
        return Map.of(
                "demoApiKey", DataSeeder.DEMO_API_KEY,
                "login", "admin@dispatchgrid.demo / password",
                "failureInjection", failureInjectionState.snapshot());
    }
}
