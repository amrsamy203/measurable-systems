package com.amrsamy.caseflow.adapters.web;

import com.amrsamy.caseflow.adapters.metrics.FailureInjectionState;
import com.amrsamy.caseflow.adapters.metrics.ThroughputMetricsCollector;
import com.amrsamy.caseflow.adapters.web.dto.ApiDtos;
import com.amrsamy.caseflow.adapters.web.security.CaseFlowUserPrincipal;
import com.amrsamy.caseflow.application.service.CasePipelineService;
import com.amrsamy.caseflow.application.service.DemoSimulationService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DemoMetricsController {

    private final DemoSimulationService demoSimulationService;
    private final ThroughputMetricsCollector metricsCollector;
    private final FailureInjectionState failureInjectionState;
    private final CasePipelineService casePipelineService;

    public DemoMetricsController(
            DemoSimulationService demoSimulationService,
            ThroughputMetricsCollector metricsCollector,
            FailureInjectionState failureInjectionState,
            CasePipelineService casePipelineService) {
        this.demoSimulationService = demoSimulationService;
        this.metricsCollector = metricsCollector;
        this.failureInjectionState = failureInjectionState;
        this.casePipelineService = casePipelineService;
    }

    @PostMapping("/demo/simulate")
    public ApiDtos.SimulateResponse simulate(
            @AuthenticationPrincipal CaseFlowUserPrincipal principal,
            @RequestBody(required = false) ApiDtos.SimulateRequest request) {

        int count = request != null && request.count() != null ? request.count() : 100;
        List<Long> ids = demoSimulationService.simulate(principal.getTenantId(), count, principal.getId());
        return new ApiDtos.SimulateResponse(ids.size(), ids);
    }

    @PostMapping("/demo/failure-injection")
    public ApiDtos.MessageResponse failureInjection(@Valid @RequestBody ApiDtos.FailureInjectionRequest request) {
        long slow = request.slowProcessingMs() != null ? request.slowProcessingMs() : 0;
        int fail = request.failurePercent() != null ? request.failurePercent() : 0;
        failureInjectionState.configure(slow, fail);
        return new ApiDtos.MessageResponse(
                "Failure injection set: slowProcessingMs=" + slow + ", failurePercent=" + fail);
    }

    @GetMapping("/metrics/throughput")
    public ApiDtos.ThroughputResponse throughput(@AuthenticationPrincipal CaseFlowUserPrincipal principal) {
        Map<String, Long> depths = casePipelineService.queueDepths(principal.getTenantId());
        long totalDepth = depths.values().stream().mapToLong(Long::longValue).sum();
        ThroughputMetricsCollector.ThroughputSnapshot snap = metricsCollector.snapshot(totalDepth);
        return new ApiDtos.ThroughputResponse(
                snap.eventsProcessed(),
                snap.eventsPerSecond(),
                snap.p95LatencyMs(),
                snap.queueDepth(),
                snap.failureCount(),
                snap.lastUpdated().toString());
    }
}
