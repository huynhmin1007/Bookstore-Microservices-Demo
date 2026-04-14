package com.dev.minn.notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.rabbitmq")
@Getter
@Setter
public class RabbitMQConfigProps {

    private ExchangesConfig exchanges = new ExchangesConfig();
    private QueuesConfig queues = new QueuesConfig();
    private RoutingKeysConfig routingKeys = new RoutingKeysConfig();

    @Getter
    @Setter
    public static class ExchangesConfig {
        private String notification;
        private String dlx;
    }

    @Getter
    @Setter
    public static class QueuesConfig {
        private String otp;
        private String notification;
        private String dlq;
    }

    @Getter
    @Setter
    public static class RoutingKeysConfig {
        private String otp;
        private String notification;
        private String dlq;
    }
}
