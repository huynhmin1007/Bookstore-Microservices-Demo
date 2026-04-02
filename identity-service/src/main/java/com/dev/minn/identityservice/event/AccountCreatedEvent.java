package com.dev.minn.identityservice.event;

public record AccountCreatedEvent(
        String accountId,
        String email,
        String firstName,
        String lastName
) {
}
