package com.dev.minn.order.entity;

import com.dev.minn.common.messaging.entity.BaseEntity;
import com.dev.minn.common.messaging.entity.OutboxEvent;
import com.dev.minn.order.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_order_customer", columnList = "customer_id")
        }
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends BaseEntity<UUID> {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "customer_id", nullable = false)
    String customerId;

    @Column(name = "payment_method", nullable = false)
    String paymentMethod;

    @Column(name = "status", nullable = false)
    OrderStatus status;

    @Column(name = "total_amount", nullable = false)
    Double totalAmount;
}
