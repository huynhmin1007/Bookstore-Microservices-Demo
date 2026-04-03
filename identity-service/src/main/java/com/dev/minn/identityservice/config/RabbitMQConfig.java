package com.dev.minn.identityservice.config;

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
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue profileSuccessQueue() {
        return new Queue(props.getQueue().getProfileSuccess());
    }

    @Bean
    public Queue profileFailedQueue() {
        return new Queue(props.getQueue().getProfileFailed());
    }

    @Bean
    public Binding bindProfileSuccess(Queue profileSuccessQueue, TopicExchange identityExchange) {
        return BindingBuilder
                .bind(profileSuccessQueue)
                .to(identityExchange)
                .with(props.getRoutingKey().getProfileSuccess());
    }

    @Bean
    public Binding bindProfileFailed(Queue profileFailedQueue, TopicExchange identityExchange) {
        return BindingBuilder
                .bind(profileFailedQueue)
                .to(identityExchange)
                .with(props.getRoutingKey().getProfileFailed());
    }
}