package com.amrsamy.caseflow.adapters.messaging;

import com.amrsamy.caseflow.application.port.EventIngestPort;
import com.amrsamy.caseflow.application.service.CasePipelineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Local profile: in-process async pipeline (no RabbitMQ required).
 */
@Component
@Profile("local")
public class LocalAsyncEventIngestAdapter implements EventIngestPort {

    private static final Logger log = LoggerFactory.getLogger(LocalAsyncEventIngestAdapter.class);

    private final CasePipelineService casePipelineService;

    public LocalAsyncEventIngestAdapter(CasePipelineService casePipelineService) {
        this.casePipelineService = casePipelineService;
    }

    @Override
    @Async("caseflowExecutor")
    public void enqueueCase(Long caseId) {
        log.debug("Local async pipeline for case {}", caseId);
        try {
            casePipelineService.routeCase(caseId);
            casePipelineService.assignCase(caseId);
        } catch (Exception ex) {
            log.error("Local pipeline failed for case {}: {}", caseId, ex.getMessage());
        }
    }
}
