package com.dev.minn.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
public class PreviewCheckoutRequest {

    @Valid
    @Size(min = 1, message = "At least one item is required")
    List<Item> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Item {

        @NotBlank(message = "Product ID is required")
        String productId;

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity;
    }
}
