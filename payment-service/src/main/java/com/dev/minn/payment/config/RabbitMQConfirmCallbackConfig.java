package com.dev.minn.payment.config;

import com.dev.minn.common.messaging.entity.OutboxEvent;
import com.dev.minn.common.messaging.repository.OutboxRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMQConfirmCallbackConfig {

    RabbitTemplate rabbitTemplate;
    OutboxRepository outboxRepository;

    @PostConstruct
    public void setupCallbacks() {
        rabbitTemplate.setConfirmCallback(((correlationData, ack, cause) -> {
            UUID outboxId = UUID.fromString(correlationData.getId());

            if (ack) {
                outboxRepository.findById(outboxId)
                        .ifPresent(event -> {
                            event.setStatus(OutboxEvent.OutboxStatus.PUBLISHED);
                            outboxRepository.save(event);
                        });
                log.info("Event {} published successfully", outboxId);
            } else {
                outboxRepository.findById(outboxId)
                        .ifPresent(event -> {
                            event.setStatus(OutboxEvent.OutboxStatus.FAILED);
                        });
                log.error("Event {} publishing failed: {}", outboxId, cause);
            }
        }));
    }
}
