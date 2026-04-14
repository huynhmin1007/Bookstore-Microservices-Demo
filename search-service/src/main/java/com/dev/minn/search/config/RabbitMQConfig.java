package com.dev.minn.search.config;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMQConfig {

    RabbitMQConfigProps props;

    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(props.getExchange().getDlx());
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(props.getQueue().getDlq()).build();
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(props.getRoutingKey().getDlq());
    }

    @Bean
    public Queue highPriorityQueue() {
        return QueueBuilder.durable(props.getQueue().getHighPriority())
                .withArgument("x-dead-letter-exchange", props.getExchange().getDlx())
                .withArgument("x-dead-letter-routing-key", props.getRoutingKey().getDlq())
                .build();
    }

    @Bean
    public Queue lowPriorityQueue() {
        return QueueBuilder.durable(props.getQueue().getLowPriority())
                .withArgument("x-dead-letter-exchange", props.getExchange().getDlx())
                .withArgument("x-dead-letter-routing-key", props.getRoutingKey().getDlq())
                .build();
    }

    @Bean
    public Declarables highPriorityBindings() {
        List<Declarable> declarables = new ArrayList<>();
        Queue highQueue = highPriorityQueue();

        for (RabbitMQConfigProps.BindingInfo info : props.getBindings().getHighPriority()) {
            TopicExchange exchange = new TopicExchange(info.getExchange());
            declarables.add(exchange);
            declarables.add(BindingBuilder.bind(highQueue).to(exchange).with(info.getRoutingKey()));
        }

        return new Declarables(declarables);
    }

    @Bean
    public Declarables lowPriorityBindings() {
        List<Declarable> declarables = new ArrayList<>();
        Queue lowQueue = lowPriorityQueue();

        for (RabbitMQConfigProps.BindingInfo info : props.getBindings().getLowPriority()) {
            TopicExchange exchange = new TopicExchange(info.getExchange());
            declarables.add(exchange);
            declarables.add(BindingBuilder.bind(lowQueue).to(exchange).with(info.getRoutingKey()));
        }

        return new Declarables(declarables);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory batchRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        factory.setBatchListener(true);
        factory.setBatchSize(500);
        // Nếu chưa đủ 500 message mà chờ quá 3 giây thì vớt luôn
        factory.setReceiveTimeout(3000L);

        return factory;
    }
}