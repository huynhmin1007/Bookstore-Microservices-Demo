package com.dev.minn.profileservice.event;

public record ProfileFailedEvent(
        String accountId,
        String errorMessage
) {
}
