package com.dev.minn.identity.listener;

import com.dev.minn.common.event.EventEnvelope;
import com.dev.minn.identity.config.RabbitMQConfigProps;
import com.dev.minn.identity.constant.NotificationEventType;
import com.dev.minn.identity.event.NotificationDispatchEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationEventPublisher {

    RabbitMQConfigProps props;
    RabbitTemplate rabbitTemplate;
    ObjectMapper mapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationDispatchEvent event) {

        EventEnvelope envelope = EventEnvelope.builder()
                .eventType(event.getEventType().name())
                .source("identity-service")
                .timestamp(Instant.now())
                .payload(mapper.valueToTree(event.getPayload()))
                .build();

        String routingKey = getRoutingKey(event.getEventType());

        rabbitTemplate.convertAndSend(props.getExchanges().getNotification(), routingKey, envelope);

        log.info("Đã publish event {} lên RabbitMQ thành công. Routing Key: {}", event.getEventType(), routingKey);
    }

    private String getRoutingKey(NotificationEventType eventType) {
        return switch (eventType) {
            case SEND_OTP_VERIFY, PASSWORD_RESET -> "notification.otp.send";
            default -> "notification.events.send";
        };
    }
}
