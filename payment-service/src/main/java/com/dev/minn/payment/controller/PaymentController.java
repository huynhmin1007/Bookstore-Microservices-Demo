package com.dev.minn.payment.controller;

import com.dev.minn.common.messaging.contract.event.PaymentSuccessfulEvent;
import com.dev.minn.common.messaging.entity.OutboxEvent;
import com.dev.minn.common.messaging.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    OutboxRepository outboxRepository;
    ObjectMapper objectMapper;

    @PreAuthorize("#userId == authentication.name")
    @PostMapping("/{orderId}")
    public String payment(
            @RequestHeader("X-Account-Id") @P("userId") String userId,
            @PathVariable String orderId
    ) {
        try {
            PaymentSuccessfulEvent eventPayload = PaymentSuccessfulEvent.builder()
                    .paymentId(UUID.randomUUID().toString())
                    .orderId(orderId)
                    .status("SUCCESS")
                    .build();

            OutboxEvent outbox = OutboxEvent.builder()
                    .aggregateType("PAYMENT")
                    .aggregateId(orderId)
                    .eventType("PAYMENT_SUCCESS_EVT")
                    .routingKey("order.events")
                    .payload(objectMapper.writeValueAsString(eventPayload))
                    .status(OutboxEvent.OutboxStatus.PENDING)
                    .build();

            outboxRepository.save(outbox);

            return "Payment successful";
        } catch (Exception e) {
            log.error("Failed to process payment for order {}", orderId, e);
            throw new RuntimeException("Failed to process payment", e);
        }
    }
}
