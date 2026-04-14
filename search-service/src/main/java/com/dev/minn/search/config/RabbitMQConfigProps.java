package com.dev.minn.search.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.rabbitmq")
@Getter
@Setter
public class RabbitMQConfigProps {

    private ExchangeConfig exchange = new ExchangeConfig();
    private QueueConfig queue = new QueueConfig();
    private RoutingKeyConfig routingKey = new RoutingKeyConfig();
    private BindingConfig bindings = new BindingConfig();

    @Getter @Setter
    public static class ExchangeConfig {
        private String dlx;
    }

    @Getter @Setter
    public static class QueueConfig {
        private String lowPriority;
        private String highPriority;
        private String dlq;
    }

    @Getter @Setter
    public static class RoutingKeyConfig {
        private String dlq;
    }

    @Getter @Setter
    public static class BindingConfig {
        private List<BindingInfo> highPriority = new ArrayList<>();
        private List<BindingInfo> lowPriority = new ArrayList<>();
    }

    @Getter @Setter
    public static class BindingInfo {
        private String exchange;
        private String routingKey;
    }
}