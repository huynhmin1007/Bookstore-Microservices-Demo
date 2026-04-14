package com.dev.minn.identity.event;

import com.dev.minn.identity.constant.NotificationEventType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

@Getter
public class NotificationDispatchEvent extends ApplicationEvent {
    private final NotificationEventType eventType;
    private final Map<String, Object> payload;

    public NotificationDispatchEvent(Object source, NotificationEventType eventType, Map<String, Object> payload) {
        super(source);
        this.eventType = eventType;
        this.payload = payload;
    }
}
