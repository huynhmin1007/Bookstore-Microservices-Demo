package com.dev.minn.order.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    PENDING(0),                // Vừa tạo, chưa làm gì
    INVENTORY_RESERVED(1),      // Đã giữ kho thành công
    INVENTORY_REJECTED(2),      // Kho từ chối (hết hàng)
    WAITING_FOR_PAYMENT(3),     // Đang đợi Webhook từ VNPay/Momo (Với thanh toán Online)
    PAID(4),                   // Đã thanh toán thành công
    COMPLETED(5),              // Đã xong toàn bộ (đã trừ kho thật, đã báo ship)
    CANCELLING(6),             // Đang trong quá trình rollback
    CANCELLED(7);             // Đã hủy hoàn toàn

    private final int status;

    public static OrderStatus fromStatus(Integer status) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.getStatus() == status) {
                return orderStatus;
            }
        }
        return null;
    }

    public static OrderStatus fromStatus(String status) {
        if (status == null)
            return null;

        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.name().equalsIgnoreCase(status)) {
                return orderStatus;
            }
        }
        return null;
    }
}
