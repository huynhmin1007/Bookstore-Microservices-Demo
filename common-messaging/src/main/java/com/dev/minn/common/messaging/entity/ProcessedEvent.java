package com.dev.minn.common.messaging.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(
        name = "processed_events",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_message_consumer", columnNames = {"message_id", "consumer_name"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "message_id", nullable = false, length = 50)
    String messageId; // Trùng với ID bảng Outbox

    @Column(name = "consumer_name", nullable = false, length = 100)
    String consumerName; // ReserveInventoryHandler

    @CreatedDate
    @Column(name = "processed_at", nullable = false, updatable = false)
    Instant processedAt;
}
