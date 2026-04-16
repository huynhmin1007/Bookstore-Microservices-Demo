package com.dev.minn.common.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MessageEnvelope<T> {

    @Builder.Default
    String messageId = UUID.randomUUID().toString();

    // Dùng cho Saga Tracking = Order ID
    String correlationId;

    // "RESERVE_INVENTORY_CMD", "INVENTORY_RESERVED_EVT"
    String messageType;

    String source;

    @Builder.Default
    Instant timestamp = Instant.now();

    T payload;
}
