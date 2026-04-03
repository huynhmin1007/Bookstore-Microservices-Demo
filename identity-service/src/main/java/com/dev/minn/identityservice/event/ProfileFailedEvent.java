package com.dev.minn.identityservice.event;

public record ProfileFailedEvent(
        String accountId,
        String errorMessage
) {
}
