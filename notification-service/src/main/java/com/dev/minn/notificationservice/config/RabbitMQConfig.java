package com.dev.minn.notificationservice.config;

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
    public TopicExchange notificationExchange() {
        return new TopicExchange(props.getExchange().getNotification());
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(props.getQueue().getNotification());
    }

    @Bean
    public Queue accountQueue() {
        return new Queue(props.getQueue().getAccount());
    }

    @Bean
    public Queue otpQueue() {
        return new Queue(props.getQueue().getOtp());
    }


    @Bean
    public Binding bindAccountEvents() {
        return BindingBuilder
                .bind(accountQueue())
                .to(notificationExchange())
                .with(props.getRoutingKey().getAccount());
    }

    @Bean
    public Binding bindOtpEvents() {
        return BindingBuilder
                .bind(otpQueue())
                .to(notificationExchange())
                .with(props.getRoutingKey().getOtp());
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
