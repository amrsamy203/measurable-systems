package com.amrsamy.caseflow.application.port;

/**
 * Publishes a case into the ingest → route → assign pipeline.
 * Local profile: @Async in-process. Docker profile: RabbitMQ.
 */
public interface EventIngestPort {
    void enqueueCase(Long caseId);
}
