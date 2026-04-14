package com.dev.minn.book.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.rabbitmq")
@Getter
@Setter
public class RabbitMQConfigProps {

    private ExchangesConfig exchanges = new ExchangesConfig();

    @Getter @Setter
    public static class ExchangesConfig {
        private String book;
    }
}
