package com.dev.minn.identity.listener;

import com.dev.minn.common.event.EventEnvelope;
import com.dev.minn.identity.config.RabbitMQConfigProps;
import com.dev.minn.identity.event.UserCreatedEvent;
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
public class UserEventPublisher {

    ObjectMapper objectMapper;
    RabbitMQConfigProps props;
    RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        log.info("Publishing user created event: {}", event);
        rabbitTemplate.convertAndSend(
                props.getExchanges().getUser(),
                "user.core.created",
                new EventEnvelope(
                        "USER_CREATED",
                        "identity-service",
                        Instant.now(),
                        objectMapper.valueToTree(event)
                )
        );
    }
}
