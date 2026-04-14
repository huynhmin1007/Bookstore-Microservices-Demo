package com.dev.minn.notification.service;

import com.dev.minn.notification.client.EmailClient;
import com.dev.minn.notification.client.dto.MailResponse;
import com.dev.minn.notification.client.dto.RecipientClient;
import com.dev.minn.notification.client.dto.SendMailRequest;
import com.dev.minn.notification.client.dto.Sender;
import com.dev.minn.notification.config.AppConfigProps;
import com.dev.minn.notification.constant.Status;
import com.dev.minn.notification.node.Log;
import com.dev.minn.notification.node.Recipient;
import com.dev.minn.notification.node.Template;
import com.dev.minn.notification.repository.LogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {

    TemplateService templateService;
    HtmlBuilderService htmlBuilderService;
    LogRepository logRepository;
    EmailClient emailClient;
    AppConfigProps appProps;

    public MailResponse sendEmail(String templateCode, Map<String, Object> payload, RecipientClient recipient) {
        Template template = templateService.findTemplate(templateCode);
        String html = htmlBuilderService.buildHtml(template.getHtmlContent(), payload);

        SendMailRequest request = SendMailRequest.builder()
                .sender(getSender())
                .to(List.of(recipient))
                .subject(template.getSubject())
                .htmlContent(html)
                .build();

        try {
            MailResponse response = emailClient.sendEmail(appProps.getKeys().getBrevo(), request);

            logRepository.save(Log.builder()
                    .recipient(new Recipient(recipient.getName(), recipient.getEmail()))
                    .templateCode(templateCode)
                    .payload(payload)
                    .status(Status.SENT)
                    .messageId(response.getMessageId())
                    .build());

            return response;

        } catch (Exception e) {
            log.error("Brevo API Error - Failed to send email to {}: {}", recipient.getEmail(), e.getMessage());

            logRepository.save(Log.builder()
                    .recipient(new Recipient(recipient.getName(), recipient.getEmail()))
                    .templateCode(templateCode)
                    .payload(payload)
                    .status(Status.FAILED)
                    .errorLog(e.getMessage())
                    .build());

            throw new AmqpRejectAndDontRequeueException("Brevo API Error - Failed to send email", e);
        }
    }

    public Sender getSender() {
        return new Sender(appProps.getEmail(), appProps.getName());
    }
}
