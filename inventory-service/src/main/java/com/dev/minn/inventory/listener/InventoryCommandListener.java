package com.dev.minn.inventory.listener;

import com.dev.minn.common.event.EventEnvelope;
import com.dev.minn.common.messaging.dto.MessageEnvelope;
import com.dev.minn.common.messaging.entity.ProcessedEvent;
import com.dev.minn.common.messaging.repository.OutboxRepository;
import com.dev.minn.common.messaging.repository.ProcessedEventRepository;
import com.dev.minn.common.messaging.strategy.EventHandlerStrategy;
import com.dev.minn.inventory.config.RabbitMQConfig;
import com.dev.minn.inventory.service.InventoryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class InventoryCommandListener {

    List<EventHandlerStrategy<?>> strategies;

    InventoryService inventoryService;
    OutboxRepository outboxRepository;
    ProcessedEventRepository processedEventRepository;
    ObjectMapper objectMapper;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.INVENTORY_CMD_QUEUE)
    public void handleCommand(MessageEnvelope<JsonNode> envelope) {
        String messageId = envelope.getMessageId();

        if (processedEventRepository.existsByMessageIdAndConsumerName(messageId, "InventoryListener")) {
            log.warn("Message {} has already been processed", messageId);
            return;
        }

        EventHandlerStrategy strategy = strategies.stream()
                .filter(s -> s.supports(envelope.getMessageType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Event type not match " + envelope.getMessageType()));

        try {
            Object typedPayload = objectMapper.treeToValue(envelope.getPayload(), strategy.getPayloadClass());

            MessageEnvelope typedEnvelope = MessageEnvelope.builder()
                    .messageId(envelope.getMessageId())
                    .correlationId(envelope.getCorrelationId())
                    .messageType(envelope.getMessageType())
                    .source(envelope.getSource())
                    .timestamp(envelope.getTimestamp())
                    .payload(typedPayload)
                    .build();

            strategy.handle(typedEnvelope);

        } catch (Exception e) {
            log.error("Failed to process command: {}", envelope.getCorrelationId(), e);
            throw new RuntimeException("Lỗi xử lý command", e);
        }

        processedEventRepository.save(ProcessedEvent.builder()
                .messageId(messageId)
                .consumerName("InventoryListener")
                .build()
        );
    }

    @Transactional
    @RabbitListener(queues = "inventory.create.queue")
    public void handleInventoryCreatedEvent(EventEnvelope envelope) {
        log.info("Received inventory created event: {}", envelope.getEventType());

        try {
            if (!"BOOK_CREATED".equalsIgnoreCase(envelope.getEventType())) {
                return;
            }

            JsonNode payload = envelope.getPayload();

            String bookId = payload.get("id").asText();
            int quantity = payload.get("quantity").asInt();
            double price = payload.get("price").asDouble();

            inventoryService.addItemToInventory(String.valueOf(bookId), quantity, price);
        } catch (Exception e) {
            log.error("Failed to process inventory created event: {}", envelope.getEventType(), e);
            throw new RuntimeException("Failed to process inventory created event", e);
        }
    }
}
