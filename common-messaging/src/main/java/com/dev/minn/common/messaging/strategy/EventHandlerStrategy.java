package com.dev.minn.common.messaging.strategy;

import com.dev.minn.common.messaging.dto.MessageEnvelope;

public interface EventHandlerStrategy<T> {

    boolean supports(String eventType);
    Class<T> getPayloadClass();
    void handle(MessageEnvelope<T> envelope) throws Exception;
}
