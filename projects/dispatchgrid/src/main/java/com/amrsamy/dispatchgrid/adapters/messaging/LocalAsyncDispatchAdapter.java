package com.amrsamy.dispatchgrid.adapters.messaging;

import com.amrsamy.dispatchgrid.application.port.DispatchPipelinePort;
import com.amrsamy.dispatchgrid.application.service.DispatchPipelineService;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class LocalAsyncDispatchAdapter implements DispatchPipelinePort {

    private final DispatchPipelineService pipelineService;

    public LocalAsyncDispatchAdapter(DispatchPipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @Override
    @Async("dispatchExecutor")
    public void enqueueMessage(Long outboxId) {
        processOutbox(outboxId);
    }

    @Override
    public void processOutbox(Long outboxId) {
        pipelineService.processOutbox(outboxId);
    }
}
