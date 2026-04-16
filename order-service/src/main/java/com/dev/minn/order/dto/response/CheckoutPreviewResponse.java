package com.dev.minn.order.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CheckoutPreviewResponse {

    List<Item> items;
    BigDecimal totalPrice;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    @FieldDefaults(level = lombok.AccessLevel.PRIVATE)
    public static class Item {
        String productId;
        int quantity;
        BigDecimal unitPrice;
        BigDecimal subTotal;
        boolean available;
    }
}
