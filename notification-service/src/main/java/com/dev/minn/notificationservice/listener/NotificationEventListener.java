package com.dev.minn.notificationservice.listener;

import com.dev.minn.notificationservice.client.dto.Recipient;
import com.dev.minn.notificationservice.client.dto.SendMailRequest;
import com.dev.minn.notificationservice.client.dto.Sender;
import com.dev.minn.notificationservice.config.EmailConfigProps;
import com.dev.minn.notificationservice.event.SendNotificationEvent;
import com.dev.minn.notificationservice.exception.CodeException;
import com.dev.minn.notificationservice.node.EventTemplateMapping;
import com.dev.minn.notificationservice.node.Template;
import com.dev.minn.notificationservice.repository.EventTemplateMappingRepository;
import com.dev.minn.notificationservice.repository.LogRepository;
import com.dev.minn.notificationservice.repository.TemplateRepository;
import com.dev.minn.notificationservice.service.EmailService;
import com.dev.minn.notificationservice.service.HtmlBuilderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class NotificationEventListener {

    EventTemplateMappingRepository eventTemplateMappingRepository;
    TemplateRepository templateRepository;
    LogRepository logRepository;
    HtmlBuilderService htmlBuilderService;
    EmailConfigProps emailProps;
    EmailService emailService;
    ObjectMapper objectMapper;

    @RabbitListener(
            queues = "${app.rabbitmq.queue.otp}",
            concurrency = "5-10"
    )
    public void handleOtpEvents(SendNotificationEvent event, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        processEmail(event, routingKey);
    }

    @RabbitListener(
            queues = "${app.rabbitmq.queue.account}",
            concurrency = "2-5"
    )
    public void handleAccountEvents(SendNotificationEvent event, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        processEmail(event, routingKey);
    }

    private void processEmail(SendNotificationEvent event, String routingKey) {
        try {
            log.info("Received event: {}", routingKey);

            EventTemplateMapping mapping = eventTemplateMappingRepository.findByEventAndIsActiveTrue(routingKey)
                    .orElse(null);

            if (mapping == null) {
                log.error("Event not found: {}", routingKey);
                return;
            }

            Template template = templateRepository.findByCode(mapping.getTemplateCode())
                    .orElseThrow(CodeException.TEMPLATE_NOT_FOUND::throwException);

            String recipientEmail = event.recipientEmail();
            Map<String, Object> payload = event.payload();

            if (recipientEmail == null || recipientEmail.isBlank()) {
                log.error("Recipient email is missing for event: {}", routingKey);
                return;
            }

            String html = htmlBuilderService.buildHtml(template.getHtmlContent(), payload);

            Object nameObj = payload.get("name");
            String recipientName = (nameObj != null && !nameObj.toString().isBlank())
                    ? nameObj.toString() : null;

            emailService.sendEmail(SendMailRequest.builder()
                    .sender(new Sender(emailProps.getSystem().getEmail(), emailProps.getSystem().getName()))
                    .to(List.of(new Recipient(recipientEmail, recipientName)))
                    .subject(template.getSubject())
                    .htmlContent(html)
                    .build());
        } catch (Exception e) {
            log.error("Error occurred while processing message: {}", e.getMessage());
        }
    }
}
