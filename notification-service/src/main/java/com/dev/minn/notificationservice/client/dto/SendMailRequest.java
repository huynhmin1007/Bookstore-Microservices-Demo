package com.dev.minn.notificationservice.client.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SendMailRequest {

    @Valid
    Sender sender;

    @Size(min = 1, message = "At least one recipient is required")
    @Valid
    List<Recipient> to;

    @NotBlank(message = "Subject is required")
    String subject;

    @NotBlank(message = "HTML content is required")
    String htmlContent;
}
