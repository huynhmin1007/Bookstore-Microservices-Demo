package com.dev.minn.inventory.strategy.impl;

import com.dev.minn.common.messaging.contract.command.ReserveInventoryCommand;
import com.dev.minn.common.messaging.contract.event.InventoryReserveFailedEvent;
import com.dev.minn.common.messaging.contract.event.InventoryReservedEvent;
import com.dev.minn.common.messaging.dto.MessageEnvelope;
import com.dev.minn.common.messaging.entity.OutboxEvent;
import com.dev.minn.common.messaging.repository.OutboxRepository;
import com.dev.minn.common.messaging.strategy.EventHandlerStrategy;
import com.dev.minn.inventory.service.InventoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ReserveInventoryStrategy implements EventHandlerStrategy<ReserveInventoryCommand> {

    ObjectMapper objectMapper;
    OutboxRepository outboxRepository;
    InventoryService inventoryService;

    @Override
    public boolean supports(String eventType) {
        return "RESERVE_INVENTORY_CMD".equalsIgnoreCase(eventType);
    }

    @Override
    public Class<ReserveInventoryCommand> getPayloadClass() {
        return ReserveInventoryCommand.class;
    }

    @Override
    public void handle(MessageEnvelope<ReserveInventoryCommand> envelope) {
        ReserveInventoryCommand command = envelope.getPayload();
        String orderId = command.getOrderId();

        try {
            inventoryService.reserveStock(command.getItems());

            InventoryReservedEvent successEvent = new InventoryReservedEvent(orderId, "SUCCESS");
            saveOutbox(orderId, "INVENTORY_RESERVED_EVT", successEvent);

            log.info("Reserved Successful for Order: {}", orderId);

        } catch (Exception e) {
            log.error("Failed reserved for Order {}. Error: {}", orderId, e.getMessage());

            InventoryReserveFailedEvent failedEvent = new InventoryReserveFailedEvent(orderId, e.getMessage());
            saveOutbox(orderId, "INVENTORY_RESERVE_FAILED_EVT", failedEvent);
        }
    }

    private void saveOutbox(String orderId, String eventType, Object payload) {
        try {
            OutboxEvent outbox = OutboxEvent.builder()
                    .aggregateType("ORDER_SAGA")
                    .aggregateId(orderId)
                    .eventType(eventType)
                    .routingKey("order.events")
                    .payload(objectMapper.writeValueAsString(payload))
                    .status(OutboxEvent.OutboxStatus.PENDING)
                    .build();
            outboxRepository.save(outbox);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi serialize payload khi lưu outbox", e);
        }
    }
}