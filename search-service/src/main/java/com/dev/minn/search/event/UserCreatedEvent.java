package com.dev.minn.search.event;

public record UserCreatedEvent(
        String accountId,
        String profileId,
        String email,
        String fullName
) {
}
