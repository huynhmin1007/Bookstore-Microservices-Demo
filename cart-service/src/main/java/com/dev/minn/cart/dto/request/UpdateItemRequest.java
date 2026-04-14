package com.dev.minn.cart.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UpdateItemRequest {

    @Min(value = 0, message = "Quantity cannot be negative")
    int quantity;
}
