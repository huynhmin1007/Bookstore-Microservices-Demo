package com.dev.minn.cart.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartResponse {
    private String userId;
    private List<CartItemResponse> items;
}
