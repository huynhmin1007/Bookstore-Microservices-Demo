package com.dev.minn.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Entity
@Table(name = "saga_instances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SagaInstance {

    @Id
    @Column(name = "saga_id", nullable = false, length = 50)
    String sagaId; // orderId

    @Column(name = "current_state", nullable = false, length = 50)
    String currentState; // PENDING, INVENTORY_RESERVED, COMPLETED, CANCELLING...

    @Column(name = "saga_payload", columnDefinition = "TEXT")
    String sagaPayload;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;
}
