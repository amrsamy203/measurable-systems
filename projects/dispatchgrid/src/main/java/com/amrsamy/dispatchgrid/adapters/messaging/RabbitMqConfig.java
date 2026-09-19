package com.amrsamy.dispatchgrid.adapters.messaging;

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

    public static final String EXCHANGE = "dispatchgrid.events";
    public static final String QUEUE = "dispatchgrid.dispatch";
    public static final String DLQ = "dispatchgrid.dlq";
    public static final String ROUTING_KEY = "dispatch";

    @Bean
    DirectExchange dispatchExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue dispatchQueue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    @Bean
    Queue dispatchDlq() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    Binding dispatchBinding(Queue dispatchQueue, DirectExchange dispatchExchange) {
        return BindingBuilder.bind(dispatchQueue).to(dispatchExchange).with(ROUTING_KEY);
    }

    @Bean
    MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
