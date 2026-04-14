package com.dev.minn.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddItemRequest {

    @NotBlank(message = "Product ID cannot be empty")
    String productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity;
}
