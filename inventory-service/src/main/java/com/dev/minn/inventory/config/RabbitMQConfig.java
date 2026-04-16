package com.dev.minn.inventory.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String INVENTORY_CMD_QUEUE = "inventory.reserve.cmd.queue";
    public static final String BOOKSTORE_EVENT_BUS = "bookstore.topic.exchange";

    public static final String DLX_EXCHANGE = "bookstore.dlx.exchange";
    public static final String DLQ_QUEUE = "inventory.dlq.queue";

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
                .with("dlq.inventory");
    }

    @Bean
    public Queue inventoryCmdQueue() {
        return QueueBuilder
                .durable(INVENTORY_CMD_QUEUE)
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
                .bind(inventoryCmdQueue())
                .to(bookstoreEventBus())
                .with("inventory.cmd.#");
    }

    @Bean
    public Queue inventoryCreateQueue() {
        return new Queue("inventory.create.queue", true);
    }

    @Bean
    public TopicExchange bookDomainExchange() {
        return new TopicExchange("book.domain.exchange");
    }

    @Bean
    public Binding createInventoryBinding(Queue inventoryCreateQueue, TopicExchange bookDomainExchange) {
        return BindingBuilder
                .bind(inventoryCreateQueue)
                .to(bookDomainExchange)
                .with("book.core.created");
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
