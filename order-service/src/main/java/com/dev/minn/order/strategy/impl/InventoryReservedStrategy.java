package com.dev.minn.order.strategy.impl;

import com.dev.minn.common.messaging.contract.event.InventoryReservedEvent;
import com.dev.minn.common.messaging.dto.MessageEnvelope;
import com.dev.minn.common.messaging.strategy.EventHandlerStrategy;
import com.dev.minn.order.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryReservedStrategy implements EventHandlerStrategy<InventoryReservedEvent> {

    OrderService orderService;

    @Override
    public boolean supports(String eventType) {
        return "INVENTORY_RESERVED_EVT".equalsIgnoreCase(eventType);
    }

    @Override
    public Class<InventoryReservedEvent> getPayloadClass() {
        return InventoryReservedEvent.class;
    }

    @Override
    public void handle(MessageEnvelope<InventoryReservedEvent> envelope) throws Exception {
        String orderId = envelope.getCorrelationId();

        orderService.processInventoryReserved(orderId);
    }
}
