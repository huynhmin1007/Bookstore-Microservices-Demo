package com.dev.minn.order.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String BOOKSTORE_EVENT_BUS = "bookstore.topic.exchange";

    public static final String DLX_EXCHANGE = "bookstore.dlx.exchange";
    public static final String DLQ_QUEUE = "order.dlq.queue";

    public static final String ORDER_EVENTS_QUEUE = "order.events.queue";

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("dlq.order");
    }

    @Bean
    public TopicExchange bookstoreEventBus() {
        return new TopicExchange(BOOKSTORE_EVENT_BUS);
    }

    @Bean
    public Queue orderEventsQueue() {
        return QueueBuilder.durable(ORDER_EVENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dlq.order")
                .build();
    }

    @Bean
    public Binding orderEventsBinding() {
        return BindingBuilder
                .bind(orderEventsQueue())
                .to(bookstoreEventBus())
                .with("order.events.#");
    }
}