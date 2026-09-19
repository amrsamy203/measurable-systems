package com.amrsamy.dispatchgrid.application.port;

public interface DispatchPipelinePort {
    void enqueueMessage(Long outboxId);

    void processOutbox(Long outboxId);
}
