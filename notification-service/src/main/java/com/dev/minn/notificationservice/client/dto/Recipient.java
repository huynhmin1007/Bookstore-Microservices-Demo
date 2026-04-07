package com.dev.minn.notificationservice.client.dto;

import com.dev.minn.notificationservice.annotation.EmailValid;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Recipient {

    @EmailValid
    @NotBlank(message = "Email is required")
    String email;

    String name;
}
