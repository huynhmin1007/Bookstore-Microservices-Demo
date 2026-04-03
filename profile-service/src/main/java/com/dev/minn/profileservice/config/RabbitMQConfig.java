package com.dev.minn.profileservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQConfigProps props;

    @Bean
    public TopicExchange identityExchange() {
        return new TopicExchange(props.getExchange().getIdentity());
    }

    @Bean
    public Queue createdQueue() {
        return new Queue(props.getQueue().getProfileCreated());
    }

    @Bean
    public Binding bindCreated(Queue createdQueue, TopicExchange identityExchange) {
        return BindingBuilder
                .bind(createdQueue)
                .to(identityExchange)
                .with(props.getRoutingKey().getAccountCreated());
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}