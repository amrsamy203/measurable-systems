package com.amrsamy.dispatchgrid.adapters.messaging;

import com.amrsamy.dispatchgrid.application.service.DispatchPipelineService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("docker")
public class RabbitDispatchConsumer {

    private final DispatchPipelineService pipelineService;

    public RabbitDispatchConsumer(DispatchPipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE)
    public void onMessage(Long outboxId) {
        pipelineService.processOutbox(outboxId);
    }
}
