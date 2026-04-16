package com.dev.minn.order.service;

import com.dev.minn.common.exception.CodeException;
import com.dev.minn.common.messaging.contract.command.CreatePaymentCommand;
import com.dev.minn.common.messaging.contract.command.ReleaseInventoryCommand;
import com.dev.minn.common.messaging.contract.command.ReserveInventoryCommand;
import com.dev.minn.common.messaging.entity.OutboxEvent;
import com.dev.minn.common.messaging.repository.OutboxRepository;
import com.dev.minn.order.constant.OrderStatus;
import com.dev.minn.order.dto.OrderItemDto;
import com.dev.minn.order.dto.request.CreateOrderRequest;
import com.dev.minn.order.dto.response.OrderResponse;
import com.dev.minn.order.entity.Order;
import com.dev.minn.order.entity.SagaInstance;
import com.dev.minn.order.grpc.InventoryClient;
import com.dev.minn.order.mapper.OrderMapper;
import com.dev.minn.order.repository.OrderRepository;
import com.dev.minn.order.repository.SagaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class OrderService {

    OrderRepository orderRepository;
    SagaRepository sagaRepository;
    OutboxRepository outboxRepository;

    OrderMapper orderMapper;
    ObjectMapper objectMapper;

    InventoryClient inventoryClient;

    @Transactional
    public OrderResponse createOrder(String customerId, CreateOrderRequest request) {
        Order order = Order.builder()
                .customerId(customerId)
                .paymentMethod(request.getPaymentMethod())
                .status(OrderStatus.PENDING)
                .totalAmount(validateStockAndCalculateTotal(request.getItems()).doubleValue())
                .build();
        orderRepository.save(order);

        SagaInstance saga = SagaInstance.builder()
                .sagaId(order.getId().toString())
                .currentState(OrderStatus.PENDING.name())
                .sagaPayload(serialize(request.getItems()))
                .build();
        sagaRepository.save(saga);

        ReserveInventoryCommand command = new ReserveInventoryCommand(
                order.getId().toString(),
                orderMapper.toItems(request.getItems())
        );

        saveToOutbox(
                order.getId().toString(),
                "RESERVE_INVENTORY_CMD",
                "inventory.cmd",
                command
        );

        log.info("Saga started for Order: {}. Command sent to Outbox.", order.getId());

        return new OrderResponse(
                order.getId().toString(),
                "Order is being processed",
                OrderStatus.PENDING.name().toLowerCase()
        );
    }

    @Transactional
    public void processInventoryReserved(String orderId) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(CodeException.ORDER_NOT_FOUND::throwException);
        SagaInstance saga = sagaRepository.findById(orderId)
                .orElseThrow(CodeException.SAGA_NOT_FOUND::throwException);

        log.info("Order received event: INVENTORY_RESERVED_EVT. Processing Order: {}.", orderId);

        if("ONLINE".equalsIgnoreCase(order.getPaymentMethod())) {
            order.setStatus(OrderStatus.WAITING_FOR_PAYMENT);
            saga.setCurrentState(OrderStatus.WAITING_FOR_PAYMENT.name());

            log.info("Order status updated to: {}", OrderStatus.WAITING_FOR_PAYMENT.name());

            saveToOutbox(
                    orderId,
                    "CREATE_PAYMENT_CMD",
                    "payment.cmd",
                    CreatePaymentCommand.builder()
                            .orderId(orderId)
                            .userId(order.getCustomerId())
                            .paymentMethod(order.getPaymentMethod())
                            .amount(order.getTotalAmount())
                            .build());
        } else {
            completeOrder(order, saga);
        }
    }

    @Transactional
    public void processInventoryRejected(String orderId, String reason) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(CodeException.ORDER_NOT_FOUND::throwException);
        SagaInstance saga = sagaRepository.findById(orderId)
                .orElseThrow(CodeException.SAGA_NOT_FOUND::throwException);

        log.error("Order received event: INVENTORY_RESERVE_FAILED_EVT. Rejecting Order: {}. Reason: {}", orderId, reason);

        order.setStatus(OrderStatus.INVENTORY_REJECTED);
        saga.setCurrentState(OrderStatus.INVENTORY_REJECTED.name());
    }

    @Transactional
    public void processPaymentSuccess(String orderId) {
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(CodeException.ORDER_NOT_FOUND::throwException);
        SagaInstance saga = sagaRepository.findById(orderId)
                .orElseThrow(CodeException.SAGA_NOT_FOUND::throwException);

        if(order.getStatus() != OrderStatus.WAITING_FOR_PAYMENT) {
            log.warn("Order is not in WAITING_FOR_PAYMENT state. Skipping payment success event. Order: {}", orderId);
            return;
        }

        log.info("Order received event: PAYMENT_SUCCESS_EVT. Order: {}.", orderId);
        completeOrder(order, saga);
    }

    @Transactional
    public void completeOrder(Order order, SagaInstance saga) {
        String orderId = order.getId().toString();

        order.setStatus(OrderStatus.COMPLETED);
        saga.setCurrentState(OrderStatus.COMPLETED.name());

        List<ReserveInventoryCommand.Item> items = deserialize(
                saga.getSagaPayload(),
                new TypeReference<List<ReserveInventoryCommand.Item>>() {}
        );

        ReleaseInventoryCommand releaseCommand = new ReleaseInventoryCommand(orderId, items);
        saveToOutbox(orderId, "RELEASE_INVENTORY_CMD", "inventory.cmd", releaseCommand);

        log.info("Order completed. Order: {}. Inventory released.", orderId);
    }

    private void saveToOutbox(String orderId, String eventType, String routingKey, Object payload) {
        try {
            OutboxEvent outbox = OutboxEvent.builder()
                    .aggregateType("ORDER_SAGA")
                    .aggregateId(orderId)
                    .eventType(eventType)
                    .routingKey(routingKey)
                    .payload(objectMapper.writeValueAsString(payload))
                    .status(OutboxEvent.OutboxStatus.PENDING)
                    .build();
            outboxRepository.save(outbox);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed while serializing Payload Outbox", e);
        }
    }

    private <T> T deserialize(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing saga payload", e);
        }
    }

    private String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing saga payload", e);
        }
    }

    private BigDecimal validateStockAndCalculateTotal(List<OrderItemDto> items) {
        Map<String, Integer> quantityMap = items.stream()
                .collect(Collectors.toMap(
                        OrderItemDto::getProductId,
                        OrderItemDto::getQuantity
                ));

        var productDetails = inventoryClient.getProductDetails(new ArrayList<>(quantityMap.keySet()));

        return productDetails.getItemsList().stream()
                .map(grpcItem -> {
                    String pid = grpcItem.getProductId();
                    int requestedQty = quantityMap.getOrDefault(pid, 0);

                    if (!grpcItem.getIsStockAvailable() || requestedQty > grpcItem.getAvailableQuantity()) {
                        throw CodeException.OUT_OF_STOCK.throwException();
                    }

                    BigDecimal unitPrice = new BigDecimal(grpcItem.getPrice());
                    return unitPrice.multiply(BigDecimal.valueOf(requestedQty));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
