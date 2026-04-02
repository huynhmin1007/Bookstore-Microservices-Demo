package com.dev.minn.profileservice.event;

public record AccountCreatedEvent(
        String accountId,
        String email,
        String firstName,
        String lastName
) {
}
