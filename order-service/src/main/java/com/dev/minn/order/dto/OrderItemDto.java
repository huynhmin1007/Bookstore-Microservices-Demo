package com.dev.minn.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemDto {

    @NotBlank(message = "Product ID is required")
    String productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity;
}
