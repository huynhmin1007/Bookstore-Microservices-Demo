package com.dev.minn.inventory.entity;

import com.dev.minn.common.messaging.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(
        name = "inventory_items",
        indexes = {
                @Index(name = "idx_inventory_product_id", columnList = "product_id", unique = true)
        }
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class InventoryItem extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "product_id", nullable = false, length = 50, unique = true)
    String productId;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    BigDecimal price;

    @Column(name = "total_quantity", nullable = false)
    Integer totalQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    @Builder.Default
    Integer reservedQuantity = 0;

    // Cơ chế Khóa Lạc Quan (Optimistic Lock) chống bán lố (Overselling)
    @Version
    @Column(name = "version")
    Long version;

    @Transient // Ko luu len db
    public Integer getAvailableQuantity() {
        return totalQuantity - reservedQuantity;
    }

    public void reserve(int quantity) {
        if(getAvailableQuantity() < quantity)
            throw new IllegalArgumentException("Not enough quantity");
        this.reservedQuantity += quantity;
    }

    public void release(int quantity) {
        if(reservedQuantity < quantity)
            throw new IllegalArgumentException("Release quantity exceeds reserved quantity");
        this.reservedQuantity -= quantity;
    }

    public void confirmDeduct(int quantity) {
        if(totalQuantity < quantity || reservedQuantity < quantity)
            throw new IllegalArgumentException("Deduct quantity exceeds total or reserved quantity");
        this.reservedQuantity -= quantity;
        this.totalQuantity -= quantity;
    }
}
