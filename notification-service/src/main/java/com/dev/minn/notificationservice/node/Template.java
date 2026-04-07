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

@Document(collection = "templates")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Template {

    @Id
    @Field(value = "id")
    String id;

    @Indexed(unique = true)
    @Field(value = "code")
    String code;

    @Field(value = "subject")
    String subject;

    @Field(value = "html_content")
    String htmlContent;

    @Field(value = "channel")
    String channel;

    @Field(value = "is_active")
    boolean isActive;
}
