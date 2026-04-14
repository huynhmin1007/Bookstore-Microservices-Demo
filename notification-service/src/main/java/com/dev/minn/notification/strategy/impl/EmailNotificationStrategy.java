package com.dev.minn.notification.strategy.impl;

import com.dev.minn.common.strategy.EventHandlerStrategy;
import com.dev.minn.notification.client.dto.RecipientClient;
import com.dev.minn.notification.service.EmailService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailNotificationStrategy implements EventHandlerStrategy {

    EmailService mailService;

    private static final List<String> EMAIL_EVENTS = List.of(
            "SEND_OTP_VERIFY",
            "USER_WELCOME",
            "PASSWORD_RESET"
    );

    @Override
    public boolean supports(String eventType) {
        return EMAIL_EVENTS.contains(eventType);
    }

    @Override
    public void handle(JsonNode payload) throws Exception {

    }

    @Override
    public void handle(JsonNode payload, String eventType) throws Exception {
        String email = payload.get("email").asText();
        String name = payload.has("name") ? payload.get("name").asText() : "User";
        RecipientClient recipient = new RecipientClient(email, name);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> templateData = mapper.convertValue(payload, new TypeReference<>() {
        });

        mailService.sendEmail(eventType, templateData, recipient);
    }
}
