package com.dev.minn.order.producer;

import com.dev.minn.common.messaging.dto.MessageEnvelope;
import com.dev.minn.common.messaging.entity.OutboxEvent;
import com.dev.minn.common.messaging.repository.OutboxRepository;
import com.dev.minn.order.config.RabbitMQConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class OrderOutboxRelayWorker {

    OutboxRepository outboxRepository;
    RabbitTemplate rabbitTemplate;
    ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 2000)
    public void relayPendingMessage() {
        List<OutboxEvent> pendingEvents = outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxEvent.OutboxStatus.PENDING);

        if(pendingEvents.isEmpty()) return;

        for(OutboxEvent event : pendingEvents) {
            try {
                MessageEnvelope<Object> envelope = MessageEnvelope.builder()
                        .messageId(event.getId().toString())
                        .correlationId(event.getAggregateId())
                        .messageType(event.getEventType())
                        .source("order-service")
                        .payload(objectMapper.readTree(event.getPayload()))
                        .build();

                CorrelationData correlationData = new CorrelationData(event.getId().toString());

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.BOOKSTORE_EVENT_BUS,
                        event.getRoutingKey(),
                        envelope,
                        correlationData
                );
            } catch (Exception e) {
                log.error("Failed when relay message ID: {}", event.getId(), e);
            }
        }
    }
}
