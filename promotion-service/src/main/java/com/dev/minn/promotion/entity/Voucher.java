package com.dev.minn.promotion.entity;

import com.dev.minn.common.messaging.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vouchers", indexes = {
        @Index(name = "idx_voucher_code", columnList = "code", unique = true)
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Voucher extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    String code;

    @Column(name = "discount_type", nullable = false)
    @Enumerated(EnumType.STRING)
    DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    BigDecimal discountValue;

    @Column(name = "max_discount_amount", precision = 12, scale = 2)
    BigDecimal maxDiscountAmount;

    @Column(name = "min_order_amount", precision = 12, scale = 2)
    BigDecimal minOrderAmount;

    Instant startDate;
    Instant endDate;

    Integer usageLimit;
    Integer currentUsage;

    @Column(name = "is_active", nullable = false)
    Boolean isActive;

    @Version
    Long version;

    public enum DiscountType {
        FIXED_AMOUNT, PERCENTAGE
    }

    public boolean isValid(BigDecimal orderAmount) {
        Instant now = Instant.now();
        return isActive
                && now.isAfter(startDate)
                && now.isBefore(endDate)
                && currentUsage < usageLimit
                && orderAmount.compareTo(minOrderAmount) >= 0;
    }
}
