package com.dev.minn.notificationservice.client.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MailResponse {

    String messageId;
}
