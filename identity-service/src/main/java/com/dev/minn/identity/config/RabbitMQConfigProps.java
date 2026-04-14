package com.dev.minn.identity.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "app.rabbitmq")
@RefreshScope
@Getter
@Setter
public class RabbitMQConfigProps {

    private ExchangesConfig exchanges = new ExchangesConfig();
    private QueuesConfig queues = new QueuesConfig();
    private RoutingKeysConfig routingKeys = new RoutingKeysConfig();

    @Getter
    @Setter
    public static class ExchangesConfig {
        String notification;
        String user;
    }

    @Getter
    @Setter
    public static class QueuesConfig {

    }

    @Getter
    @Setter
    public static class RoutingKeysConfig {
        String otp;
        String notification;
    }
}
