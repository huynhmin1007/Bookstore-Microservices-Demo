package com.dev.minn.common.strategy;

import com.dev.minn.common.event.EventEnvelope;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface EventHandlerStrategy {

    boolean supports(String eventType);

    void handle(JsonNode payload) throws Exception;
    void handle(JsonNode payload, String eventType) throws Exception;

    default void handleBatch(List<EventEnvelope> envelopes) throws Exception {
        for (EventEnvelope envelope : envelopes) {
            handle(envelope.getPayload());
        }
    }
}
