package com.amrsamy.caseflow.adapters.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("docker")
public class RabbitMqConfig {

    public static final String EXCHANGE = "caseflow.events";
    public static final String QUEUE_INGEST = "caseflow.ingest";
    public static final String QUEUE_ASSIGN = "caseflow.assign";
    public static final String QUEUE_DLQ = "caseflow.dlq";
    public static final String ROUTING_INGEST = "ingest";
    public static final String ROUTING_ASSIGN = "assign";
    public static final String ROUTING_DLQ = "dlq";

    @Bean
    public DirectExchange caseflowExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue ingestQueue() {
        return QueueBuilder.durable(QUEUE_INGEST)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ROUTING_DLQ)
                .build();
    }

    @Bean
    public Queue assignQueue() {
        return QueueBuilder.durable(QUEUE_ASSIGN)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ROUTING_DLQ)
                .build();
    }

    @Bean
    public Queue dlq() {
        return QueueBuilder.durable(QUEUE_DLQ).build();
    }

    @Bean
    public Binding ingestBinding(Queue ingestQueue, DirectExchange caseflowExchange) {
        return BindingBuilder.bind(ingestQueue).to(caseflowExchange).with(ROUTING_INGEST);
    }

    @Bean
    public Binding assignBinding(Queue assignQueue, DirectExchange caseflowExchange) {
        return BindingBuilder.bind(assignQueue).to(caseflowExchange).with(ROUTING_ASSIGN);
    }

    @Bean
    public Binding dlqBinding(Queue dlq, DirectExchange caseflowExchange) {
        return BindingBuilder.bind(dlq).to(caseflowExchange).with(ROUTING_DLQ);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate(
            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory,
            MessageConverter jacksonMessageConverter) {
        org.springframework.amqp.rabbit.core.RabbitTemplate template =
                new org.springframework.amqp.rabbit.core.RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter);
        return template;
    }
}
