package com.dev.minn.order.controller;

import com.dev.minn.order.dto.request.CreateOrderRequest;
import com.dev.minn.order.dto.response.OrderResponse;
import com.dev.minn.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;

    @PostMapping()
    @PreAuthorize("@iam.check('order:order:create') or #userId == authentication.name")
    public OrderResponse createOrder(
            @RequestHeader("X-Account-Id") @P("userId") String userId,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return orderService.createOrder(userId, request);
    }
}
