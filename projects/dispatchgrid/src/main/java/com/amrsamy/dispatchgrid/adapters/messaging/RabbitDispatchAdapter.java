package com.amrsamy.dispatchgrid.adapters.messaging;

import com.amrsamy.dispatchgrid.application.port.DispatchPipelinePort;
import com.amrsamy.dispatchgrid.application.service.DispatchPipelineService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("docker")
public class RabbitDispatchAdapter implements DispatchPipelinePort {

    private final RabbitTemplate rabbitTemplate;
    private final DispatchPipelineService pipelineService;

    public RabbitDispatchAdapter(RabbitTemplate rabbitTemplate, DispatchPipelineService pipelineService) {
        this.rabbitTemplate = rabbitTemplate;
        this.pipelineService = pipelineService;
    }

    @Override
    public void enqueueMessage(Long outboxId) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.ROUTING_KEY, outboxId);
    }

    @Override
    public void processOutbox(Long outboxId) {
        pipelineService.processOutbox(outboxId);
    }
}
