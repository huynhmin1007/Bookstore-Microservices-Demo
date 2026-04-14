package com.dev.minn.identity.event;

public record UserCreatedEvent(
        String accountId,
        String profileId,
        String email,
        String fullName
) {
}
