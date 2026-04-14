package com.dev.minn.notification.listener;

import com.dev.minn.common.event.EventEnvelope;
import com.dev.minn.common.strategy.EventHandlerStrategy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationListener {

    List<EventHandlerStrategy> strategies;

    @RabbitListener(queues = "${app.rabbitmq.queues.otp}")
    public void handleOtpEvent(EventEnvelope envelope) {
        log.info("[OTP Queue] Received event: {}", envelope.getEventType());
        processEvent(envelope);
    }

    @RabbitListener(queues = "${app.rabbitmq.queues.notification}")
    public void handleNotificationEvent(EventEnvelope envelope) {
        log.info("[Notification Queue] Received event: {}", envelope.getEventType());
        processEvent(envelope);
    }

    private void processEvent(EventEnvelope envelope) {
        try {
            EventHandlerStrategy strategy = strategies.stream()
                    .filter(s -> s.supports(envelope.getEventType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Event type not match " + envelope.getEventType()));

            strategy.handle(envelope.getPayload(), envelope.getEventType());

        } catch (Exception e) {
            log.error("Lỗi xử lý event {}: {}", envelope.getEventType(), e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Thất bại khi xử lý event", e);
        }
    }
}