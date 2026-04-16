package com.dev.minn.order.listener;

import com.dev.minn.common.messaging.dto.MessageEnvelope;
import com.dev.minn.common.messaging.entity.ProcessedEvent;
import com.dev.minn.common.messaging.repository.ProcessedEventRepository;
import com.dev.minn.common.messaging.strategy.EventHandlerStrategy;
import com.dev.minn.order.config.RabbitMQConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderEventListener {

    List<EventHandlerStrategy<?>> strategies;
    ProcessedEventRepository processedEventRepository;
    ObjectMapper objectMapper;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.ORDER_EVENTS_QUEUE)
    public void handleOrderEvent(MessageEnvelope<JsonNode> rawEnvelope) {
        String messageId = rawEnvelope.getMessageId();
        String eventType = rawEnvelope.getMessageType();

        if (processedEventRepository.existsByMessageIdAndConsumerName(messageId, "OrderListener")) {
            log.warn("Message {} has already been processed", messageId);
            return;
        }

        EventHandlerStrategy strategy = strategies.stream()
                .filter(s -> s.supports(eventType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Event type not match " + eventType));

        try {
            Object typedPayload = objectMapper.treeToValue(rawEnvelope.getPayload(), strategy.getPayloadClass());

            MessageEnvelope typedEnvelope = MessageEnvelope.builder()
                    .messageId(messageId)
                    .correlationId(rawEnvelope.getCorrelationId())
                    .messageType(eventType)
                    .source(rawEnvelope.getSource())
                    .timestamp(rawEnvelope.getTimestamp())
                    .payload(typedPayload)
                    .build();

            strategy.handle(rawEnvelope);
        } catch (Exception e) {
            log.error("Failed to process event: {}, Order: {}", eventType, e, rawEnvelope.getCorrelationId());
            throw new RuntimeException("Failed to process event", e);
        }

        processedEventRepository.save(ProcessedEvent.builder()
                .messageId(messageId)
                .consumerName("OrderEventListener")
                .build()
        );
    }
}
