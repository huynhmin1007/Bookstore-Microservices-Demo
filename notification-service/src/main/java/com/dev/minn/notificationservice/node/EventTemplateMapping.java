package com.dev.minn.notificationservice.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "event_template_mappings")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class EventTemplateMapping {

    @Id
    @Field(value = "id")
    String id;

    @Indexed
    @Field(value = "event")
    String event;

    @Field(value = "template_code")
    String templateCode;

    @Field(value = "is_active")
    boolean isActive;
}
