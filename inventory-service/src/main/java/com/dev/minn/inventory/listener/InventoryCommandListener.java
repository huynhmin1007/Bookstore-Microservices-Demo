package com.dev.minn.inventory.listener;

import com.dev.minn.common.event.EventEnvelope;
import com.dev.minn.common.messaging.contract.command.ReserveInventoryCommand;
import com.dev.minn.common.messaging.contract.event.InventoryReserveFailedEvent;
import com.dev.minn.common.messaging.contract.event.InventoryReservedEvent;
import com.dev.minn.common.messaging.dto.MessageEnvelope;
import com.dev.minn.common.messaging.entity.OutboxEvent;
import com.dev.minn.common.messaging.entity.ProcessedEvent;
import com.dev.minn.common.messaging.repository.OutboxRepository;
import com.dev.minn.common.messaging.repository.ProcessedEventRepository;
import com.dev.minn.inventory.config.RabbitMQConfig;
import com.dev.minn.inventory.service.InventoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class InventoryCommandListener {

    InventoryService inventoryService;
    OutboxRepository outboxRepository;
    ProcessedEventRepository processedEventRepository;
    ObjectMapper objectMapper;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.INVENTORY_CMD_QUEUE)
    public void handleReserveCommand(MessageEnvelope<ReserveInventoryCommand> envelope) {
        String messageId = envelope.getMessageId();
        String orderId = envelope.getCorrelationId();

        if (processedEventRepository.existsByMessageIdAndConsumerName(messageId, "InventoryListener")) {
            log.warn("Message {} has already been processed", messageId);
            return;
        }

        try {
            inventoryService.reverseStock(envelope.getPayload().getItems());
            saveToOutbox(orderId, "INVENTORY_RESERVED_EVT", new InventoryReservedEvent(orderId, "SUCCESS"));
            log.info("Reserve inventory command processed successfully: {}", orderId);
        } catch (Exception e) {
            log.error("Failed to process reserve inventory command: {}", orderId, e);
            saveToOutbox(orderId, "INVENTORY_RESERVED_FAILED_EVT", new InventoryReserveFailedEvent(orderId, e.getMessage()));
        }

        processedEventRepository.save(ProcessedEvent.builder()
                .messageId(messageId)
                .consumerName("InventoryListener")
                .build()
        );
    }

    private void saveToOutbox(String orderId, String eventType, Object payload) {
        try {
            OutboxEvent outbox = OutboxEvent.builder()
                    .aggregateType("ORDER_SAGA")
                    .aggregateId(orderId)
                    .eventType(eventType)
                    .routingKey("order.events")
                    .payload(objectMapper.writeValueAsString(payload))
                    .status(OutboxEvent.OutboxStatus.PENDING)
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed while serializing Payload Outbox", e);
        }
    }

    @Transactional
    @RabbitListener(queues = "inventory.create.queue")
    public void handleInventoryCreatedEvent(EventEnvelope envelope) {
        log.info("Received inventory created event: {}", envelope.getEventType());

        try {
            if(!"BOOK_CREATED".equalsIgnoreCase(envelope.getEventType())) {
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
