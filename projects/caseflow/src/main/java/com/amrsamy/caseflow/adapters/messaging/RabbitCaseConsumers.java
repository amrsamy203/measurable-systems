package com.amrsamy.caseflow.adapters.messaging;

import com.amrsamy.caseflow.application.service.CasePipelineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("docker")
public class RabbitCaseConsumers {

    private static final Logger log = LoggerFactory.getLogger(RabbitCaseConsumers.class);

    private final CasePipelineService casePipelineService;
    private final RabbitTemplate rabbitTemplate;

    public RabbitCaseConsumers(CasePipelineService casePipelineService, RabbitTemplate rabbitTemplate) {
        this.casePipelineService = casePipelineService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_INGEST)
    public void onIngest(RabbitEventIngestAdapter.CaseMessage message) {
        log.debug("Ingest consumer received case {}", message.caseId());
        casePipelineService.routeCase(message.caseId());
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.ROUTING_ASSIGN,
                message);
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_ASSIGN)
    public void onAssign(RabbitEventIngestAdapter.CaseMessage message) {
        log.debug("Assign consumer received case {}", message.caseId());
        casePipelineService.assignCase(message.caseId());
    }
}
