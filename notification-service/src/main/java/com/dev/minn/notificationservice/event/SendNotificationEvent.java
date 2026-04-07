package com.dev.minn.notificationservice.event;

import java.util.Map;

public record SendNotificationEvent(
        String recipientEmail,
        Map<String, Object> payload
) {
}
