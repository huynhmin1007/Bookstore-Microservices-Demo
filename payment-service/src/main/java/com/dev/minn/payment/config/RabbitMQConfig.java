package com.dev.minn.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PAYMENT_CMD_QUEUE = "payment.reserve.cmd.queue";
    public static final String BOOKSTORE_EVENT_BUS = "bookstore.topic.exchange";

    public static final String DLX_EXCHANGE = "bookstore.dlx.exchange";
    public static final String DLQ_QUEUE = "payment.dlq.queue";

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
                .with("dlq.payment");
    }

    @Bean
    public Queue paymentCmdQueue() {
        return QueueBuilder
                .durable(PAYMENT_CMD_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dlq.inventory")
                .build();
    }

    @Bean
    public TopicExchange bookstoreEventBus() {
        return new TopicExchange(BOOKSTORE_EVENT_BUS);
    }

    @Bean
    public Binding inventoryBinding() {
        return BindingBuilder
                .bind(paymentCmdQueue())
                .to(bookstoreEventBus())
                .with("payment.cmd.#");
    }

    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
