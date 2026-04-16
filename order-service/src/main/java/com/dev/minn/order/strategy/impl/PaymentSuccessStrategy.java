package com.dev.minn.order.strategy.impl;

import com.dev.minn.common.messaging.contract.event.PaymentSuccessfulEvent;
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
public class PaymentSuccessStrategy implements EventHandlerStrategy<PaymentSuccessfulEvent> {

    OrderService orderService;

    @Override
    public boolean supports(String eventType) {
        return "PAYMENT_SUCCESS_EVT".equalsIgnoreCase(eventType);
    }

    @Override
    public Class<PaymentSuccessfulEvent> getPayloadClass() {
        return PaymentSuccessfulEvent.class;
    }

    @Override
    public void handle(MessageEnvelope<PaymentSuccessfulEvent> envelope) throws Exception {
        String orderId = envelope.getCorrelationId();

        orderService.processPaymentSuccess(orderId);
    }
}
