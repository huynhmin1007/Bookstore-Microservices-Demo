package com.dev.minn.identityservice.config;

import com.dev.minn.identityservice.service.DataInitService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    @NonFinal
    @Value("${data.account.admin.email}")
    String ADMIN_EMAIL;

    @NonFinal
    @Value("${data.account.admin.password}")
    String ADMIN_PASSWORD;

    @Bean
    ApplicationRunner applicationRunner(DataInitService dataInitService) {
        return args -> {
            dataInitService.initAdminAccount(ADMIN_EMAIL, ADMIN_PASSWORD);
            log.info("Application initialization completed .....");
        };
    }
}
