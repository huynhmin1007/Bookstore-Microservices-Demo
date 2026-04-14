package com.dev.minn.cart.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {
    String productId;
    int quantity;
}
