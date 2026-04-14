package com.dev.minn.notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppConfigProps {

    String email;
    String name;
    KeysConfig keys = new KeysConfig();

    @Getter @Setter
    public static class KeysConfig {
        String brevo;
    }
}
