package com.dev.minn.notificationservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.email")
@Getter
@Setter
public class EmailConfigProps {

    private SystemConfig system = new SystemConfig();

    @Getter @Setter
    public static class SystemConfig {
        String email;
        String name;
    }
}
