package com.dev.minn.notificationservice.service;

import com.dev.minn.notificationservice.client.EmailClient;
import com.dev.minn.notificationservice.client.dto.MailResponse;
import com.dev.minn.notificationservice.client.dto.SendMailRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    @Value("${app.keys.email}")
    @NonFinal
    String API_EMAIL_KEY;

    EmailClient emailClient;

    public MailResponse sendEmail(SendMailRequest request) {
        return emailClient.sendEmail(
                API_EMAIL_KEY, request
        );
    }
}
