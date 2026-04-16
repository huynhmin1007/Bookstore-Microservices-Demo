package com.dev.minn.order.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "saga_instances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class SagaInstance {

    @Id
    @Column(name = "saga_id", nullable = false, length = 50)
    String sagaId; // orderId

    @Column(name = "current_state", nullable = false, length = 50)
    String currentState; // PENDING, INVENTORY_RESERVED, COMPLETED, CANCELLING...

    @Column(name = "saga_payload", columnDefinition = "TEXT")
    String sagaPayload;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    Instant updatedAt;
}
