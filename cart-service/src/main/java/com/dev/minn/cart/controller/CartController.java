package com.dev.minn.cart.controller;

import com.dev.minn.cart.dto.request.AddItemRequest;
import com.dev.minn.cart.dto.request.UpdateItemRequest;
import com.dev.minn.cart.dto.response.CartResponse;
import com.dev.minn.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    CartService cartService;

    @GetMapping
    @PreAuthorize("#userId == authentication.name")
    public CartResponse getCart(
            @RequestHeader("X-Account-Id") String userId
    ) {
        return cartService.getCart(userId);
    }

    @PostMapping("/items")
    @PreAuthorize("#userId == authentication.name")
    @ResponseStatus(HttpStatus.CREATED)
    public String addItem(
            @RequestHeader("X-Account-Id") String userId,
            @Valid @RequestBody AddItemRequest request
    ) {
        cartService.addItem(userId, request);
        return "Item added to cart";
    }

    @PatchMapping("/items/{productId}")
    @PreAuthorize("#userId == authentication.name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String updateItemQuantity(
            @RequestHeader("X-Account-Id") String userId,
            @PathVariable String productId,
            @Valid @RequestBody UpdateItemRequest request
    ) {
        cartService.updateItemQuantity(userId, productId, request);
        return "Item quantity updated";
    }

    @DeleteMapping("/items/{productId}")
    @PreAuthorize("#userId == authentication.name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String removeItem(
            @RequestHeader("X-Account-Id") String userId,
            @PathVariable String productId
    ) {
        cartService.removeItem(userId, productId);
        return "Item removed from cart";
    }

    @DeleteMapping
    @PreAuthorize("#userId == authentication.name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String clearCart(
            @RequestHeader("X-Account-Id") String userId
    ) {
        cartService.clearCart(userId);
        return "Cart cleared";
    }
}
