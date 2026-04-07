package com.dev.minn.identityservice.event;

import java.util.Map;

public record SendNotificationEvent(
        String recipientEmail,
        Map<String, Object> payload
) {
}
