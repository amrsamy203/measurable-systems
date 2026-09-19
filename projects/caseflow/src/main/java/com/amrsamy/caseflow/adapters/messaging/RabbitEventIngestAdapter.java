package com.amrsamy.caseflow.adapters.messaging;

import com.amrsamy.caseflow.application.port.EventIngestPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("docker")
public class RabbitEventIngestAdapter implements EventIngestPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitEventIngestAdapter.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitEventIngestAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void enqueueCase(Long caseId) {
        log.debug("Publishing case {} to RabbitMQ ingest queue", caseId);
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.ROUTING_INGEST,
                new CaseMessage(caseId));
    }

    public record CaseMessage(Long caseId) {
    }
}
