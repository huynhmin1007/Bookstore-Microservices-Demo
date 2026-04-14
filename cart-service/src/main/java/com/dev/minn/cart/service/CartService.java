package com.dev.minn.cart.service;

import com.dev.minn.cart.dto.request.AddItemRequest;
import com.dev.minn.cart.dto.request.UpdateItemRequest;
import com.dev.minn.cart.dto.response.CartItemResponse;
import com.dev.minn.cart.dto.response.CartResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

    RedisService redisService;

    static String CART_KEY_PREFIX = "cart:";
    static long CART_TTL_DAYS = 30;

    private String getCartKey(String userId) {
        return CART_KEY_PREFIX + userId;
    }

    public CartResponse getCart(String userId) {
        String key = getCartKey(userId);
        Map<Object, Object> rawCart = redisService.hashGetAll(key);

        List<CartItemResponse> items = rawCart.entrySet().stream()
                .map(entry -> CartItemResponse.builder()
                        .productId((String) entry.getKey())
                        .quantity((Integer) entry.getValue())
                        .build())
                .toList();

        return CartResponse.builder()
                .userId(userId)
                .items(items)
                .build();
    }

    public void addItem(String userId, AddItemRequest request) {
        String key = getCartKey(userId);
        String productId = request.getProductId();

        Integer currentQty = redisService.hashGet(key, productId, Integer.class);
        int newQty = (currentQty != null ? currentQty : 0) + request.getQuantity();

        redisService.hashSet(key, productId, newQty);
        updateCartTTL(userId);
    }

    public void updateItemQuantity(String userId, String productId, UpdateItemRequest request) {
        String key = getCartKey(userId);

        if(request.getQuantity() == 0) {
            redisService.hashDelete(key, productId);
        } else {
            redisService.hashSet(key, productId, request.getQuantity());
            updateCartTTL(userId);
        }
    }

    public void removeItem(String userId, String productId) {
        String key = getCartKey(userId);
        redisService.hashDelete(key, productId);
        updateCartTTL(userId);
    }

    public void clearCart(String userId) {
        String key = getCartKey(userId);
        redisService.delete(key);
    }

    private void updateCartTTL(String userId) {
        String key = getCartKey(userId);
        redisService.expire(key, CART_TTL_DAYS, TimeUnit.DAYS);
    }
}
