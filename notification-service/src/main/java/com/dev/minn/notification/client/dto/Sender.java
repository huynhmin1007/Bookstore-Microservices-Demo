package com.dev.minn.notification.client.dto;

import com.dev.minn.common.annotation.EmailValid;
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
public class Sender {

    @EmailValid
    @NotBlank(message = "Email is required")
    String email;

    @NotBlank(message = "Name is required")
    String name;
}
