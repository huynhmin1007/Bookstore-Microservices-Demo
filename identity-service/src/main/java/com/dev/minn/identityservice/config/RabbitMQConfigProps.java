package com.dev.minn.identityservice.config;

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

    @Getter
    @Setter
    public static class ExchangeConfig {
        private String identity;
    }

    @Getter
    @Setter
    public static class QueueConfig {
        private String profileSuccess;
        private String profileFailed;
    }

    @Getter
    @Setter
    public static class RoutingKeyConfig {
        private String accountCreated;
        private String profileSuccess;
        private String profileFailed;
    }
}
