package com.dev.minn.profileservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 1. Khai báo Hộp thư (Queue) dành riêng cho Profile Service
    @Bean
    public Queue profileAccountCreatedQueue() {
        return new Queue("profile.account.created.queue");
    }

    @Bean
    public TopicExchange identityExchange() {
        return new TopicExchange("identity-exchange");
    }

    // "Bất cứ tin nhắn nào vào identity-exchange mà có dán tem 'account.created'
    // thì nhét hết vào hộp thư profile.account.created.queue cho tôi"
    @Bean
    public Binding bindingAccountCreated(Queue profileAccountCreatedQueue, TopicExchange identityExchange) {
        return BindingBuilder
                .bind(profileAccountCreatedQueue)
                .to(identityExchange)
                .with("account.created");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
