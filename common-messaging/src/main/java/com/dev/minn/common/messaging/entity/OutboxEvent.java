package com.dev.minn.common.messaging.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(
        name = "outbox_events",
        indexes = {
                @Index(name = "idx_outbox_status_created", columnList = "status, created_at"),
                @Index(name = "idx_outbox_aggregate_id", columnList = "aggregate_id"),
                @Index(name = "idx_outbox_event_type", columnList = "event_type")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutboxEvent extends BaseEntity<UUID> {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    UUID id;

    @Column(name = "aggregate_type", nullable = false)
    String aggregateType; // "ORDER", "INVENTORY"

    @Column(name = "aggregate_id", nullable = false)
    String aggregateId; // "ord_12345"

    @Column(name = "event_type", nullable = false)
    String eventType; // RESERVE_INVENTORY_CMD", "ORDER_CREATED_EVT"

    @Column(name = "routing_key", nullable = false)
    String routingKey; // "inventory.cmd", "order.events"

    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    OutboxStatus status;

    @PrePersist
    public void ensureId() {
        if (id == null) {
            id = UuidCreator.getTimeOrderedEpoch();
        }
    }

    public enum OutboxStatus {
        PENDING,
        PUBLISHED,
        FAILED
    }
}
