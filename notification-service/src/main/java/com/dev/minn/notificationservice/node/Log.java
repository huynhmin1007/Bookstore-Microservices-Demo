package com.dev.minn.notificationservice.node;

import com.dev.minn.notificationservice.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

@Document(collection = "logs")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Log {

    @Id
    @Field(value = "id")
    String id;

    @Field(value = "recipient")
    Recipient recipient;

    @Field(value = "template_code")
    String templateCode;

    @Field(value = "payload")
    Map<String, Object> payload;

    @Field(value = "status")
    Status status;

    @Indexed
    @Field(value = "message_id")
    String messageId;

    @Field(value = "error_log")
    String errorLog;

    @Indexed(expireAfter = "30d")
    @CreatedDate
    @Field(value = "created_at")
    private Instant createdAt;
}
