package com.dev.minn.identityservice.config;

import com.dev.minn.identityservice.utils.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> {
            String id = SecurityUtils.getCurrentAccountId();

            if (id == null)
                return Optional.of("SYSTEM");

            return Optional.of(id);
        };
    }
}