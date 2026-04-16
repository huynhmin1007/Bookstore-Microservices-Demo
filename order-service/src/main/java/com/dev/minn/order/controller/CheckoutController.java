package com.dev.minn.order.controller;

import com.dev.minn.order.dto.request.PreviewCheckoutRequest;
import com.dev.minn.order.dto.response.CheckoutPreviewResponse;
import com.dev.minn.order.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckoutController {

    CheckoutService checkoutService;

    @PostMapping("/preview")
    public CheckoutPreviewResponse preview(
            @RequestHeader("X-Account-Id") String userId,
            @Valid @RequestBody PreviewCheckoutRequest request
            ) {
        return checkoutService.preview(userId, request);
    }
}
