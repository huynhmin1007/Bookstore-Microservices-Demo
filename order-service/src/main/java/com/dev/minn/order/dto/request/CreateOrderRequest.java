package com.dev.minn.order.dto.request;

import com.dev.minn.order.dto.OrderItemDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderRequest {

    @NotBlank(message = "Payment method is required")
    String paymentMethod;

    @Valid
    @Size(min = 1, message = "At least one item is required")
    List<OrderItemDto> items;
}
