package com.dev.minn.profileservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.rabbitmq")
@Getter
@Setter
public class RabbitMQConfigProps {

    private ExchangeConfig exchange = new ExchangeConfig();
    private QueueConfig queue = new QueueConfig();
    private RoutingKeyConfig routingKey = new RoutingKeyConfig();

    @Getter @Setter
    public static class ExchangeConfig {
        private String identity;
    }

    @Getter @Setter
    public static class QueueConfig {
        private String profileCreated; // Map với profile-created trong YAML
    }

    @Getter @Setter
    public static class RoutingKeyConfig {
        private String accountCreated;
        private String profileSuccess;
        private String profileFailed;
    }
}