package com.dev.minn.order.service;

import com.bookstore.common.grpc.inventory.ProductDetailsResponse;
import com.dev.minn.common.exception.CodeException;
import com.dev.minn.order.dto.request.PreviewCheckoutRequest;
import com.dev.minn.order.dto.response.CheckoutPreviewResponse;
import com.dev.minn.order.grpc.InventoryClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckoutService {

    InventoryClient inventoryClient;

    public CheckoutPreviewResponse preview(String userId, PreviewCheckoutRequest request) {
        Map<String, Integer> quantityMap = request.getItems().stream()
                .collect(Collectors.toMap(
                        PreviewCheckoutRequest.Item::getProductId,
                        PreviewCheckoutRequest.Item::getQuantity
                ));

        ProductDetailsResponse productDetails = inventoryClient.getProductDetails(
                new ArrayList<>(quantityMap.keySet())
        );

        List<CheckoutPreviewResponse.Item> responseItems = productDetails.getItemsList().stream()
                .map(grpcItem -> {
                    String pid = grpcItem.getProductId();
                    int requestedQty = quantityMap.getOrDefault(pid, 0);

                    if (!grpcItem.getIsStockAvailable() || requestedQty > grpcItem.getAvailableQuantity()) {
                        throw CodeException.OUT_OF_STOCK.throwException();
                    }

                    BigDecimal unitPrice = new BigDecimal(grpcItem.getPrice());
                    BigDecimal subTotal = unitPrice.multiply(BigDecimal.valueOf(requestedQty));

                    return CheckoutPreviewResponse.Item.builder()
                            .productId(pid)
                            .quantity(requestedQty)
                            .unitPrice(unitPrice)
                            .subTotal(subTotal)
                            .available(true)
                            .build();
                })
                .toList();

        BigDecimal totalPrice = responseItems.stream()
                .map(CheckoutPreviewResponse.Item::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CheckoutPreviewResponse.builder()
                .items(responseItems)
                .totalPrice(totalPrice)
                .build();
    }
}