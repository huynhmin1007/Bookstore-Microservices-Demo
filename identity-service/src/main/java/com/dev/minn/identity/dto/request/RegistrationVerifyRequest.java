package com.dev.minn.identity.dto.request;

import com.dev.minn.common.annotation.EmailValid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RegistrationVerifyRequest {
    @EmailValid
    String email;

    @NotBlank
    String otp;
}