package com.amrsamy.relateai.adapters.web;

import com.amrsamy.relateai.adapters.metrics.MetricsCollector;
import com.amrsamy.relateai.adapters.web.dto.ApiDtos;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricsCollector metricsCollector;

    public MetricsController(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    @GetMapping("/throughput")
    public ApiDtos.ThroughputResponse throughput() {
        MetricsCollector.Snapshot snap = metricsCollector.snapshot();
        return new ApiDtos.ThroughputResponse(
                snap.challengesGenerated(),
                snap.interactionsRecorded(),
                snap.followsCreated(),
                snap.topicJoins(),
                snap.challengeResponses(),
                snap.uploads(),
                snap.lastUpdated());
    }
}
