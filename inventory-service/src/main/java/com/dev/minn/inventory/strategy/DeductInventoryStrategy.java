package com.dev.minn.inventory.strategy;

import com.dev.minn.common.messaging.contract.command.DeductInventoryCommand;
import com.dev.minn.common.messaging.contract.event.InventoryFailedEvent;
import com.dev.minn.common.messaging.contract.event.InventorySuccessEvent;
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
public class DeductInventoryStrategy implements EventHandlerStrategy<DeductInventoryCommand> {

    ObjectMapper objectMapper;
    OutboxRepository outboxRepository;
    InventoryService inventoryService;

    @Override
    public boolean supports(String eventType) {
        return "DEDUCT_INVENTORY_CMD".equalsIgnoreCase(eventType);
    }

    @Override
    public Class<DeductInventoryCommand> getPayloadClass() {
        return DeductInventoryCommand.class;
    }

    @Override
    public void handle(MessageEnvelope<DeductInventoryCommand> envelope) {
        DeductInventoryCommand command = envelope.getPayload();
        String orderId = command.getOrderId();

        try {
            inventoryService.confirmDeductStock(command.getItems());

            InventorySuccessEvent successEvent = new InventorySuccessEvent(orderId, "SUCCESS");
            saveOutbox(orderId, "INVENTORY_DEDUCTED_EVT", successEvent);

            log.info("Confirm deducted Successful for Order: {}", orderId);

        } catch (Exception e) {
            log.error("Failed deduct for Order {}. Error: {}", orderId, e.getMessage());

            InventoryFailedEvent failedEvent = new InventoryFailedEvent(orderId, e.getMessage());
            saveOutbox(orderId, "INVENTORY_DEDUCTED_FAILED_EVT", failedEvent);
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