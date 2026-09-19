package com.amrsamy.dispatchgrid.adapters.messaging;

import com.amrsamy.dispatchgrid.adapters.persistence.entity.OutboxMessageEntity;
import com.amrsamy.dispatchgrid.adapters.persistence.repo.OutboxMessageRepository;
import com.amrsamy.dispatchgrid.application.port.DispatchPipelinePort;
import com.amrsamy.dispatchgrid.domain.model.OutboxStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Safety net: re-enqueue PENDING outbox rows that were rate-limited or left pending after retries.
 * Required for both local (@Async) and docker (RabbitMQ) profiles.
 */
@Component
@Profile({"local", "docker"})
public class OutboxPoller {

    private final OutboxMessageRepository outboxRepository;
    private final DispatchPipelinePort pipelinePort;
    private final int batchSize;

    public OutboxPoller(
            OutboxMessageRepository outboxRepository,
            DispatchPipelinePort pipelinePort,
            @Value("${dispatchgrid.dispatch.batch-size:50}") int batchSize) {
        this.outboxRepository = outboxRepository;
        this.pipelinePort = pipelinePort;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${dispatchgrid.dispatch.outbox-poll-ms:250}")
    public void poll() {
        List<OutboxMessageEntity> pending = outboxRepository.findByStatusOrderByCreatedAtAsc(
                OutboxStatus.PENDING, PageRequest.of(0, batchSize));
        for (OutboxMessageEntity row : pending) {
            pipelinePort.enqueueMessage(row.getId());
        }
    }
}
