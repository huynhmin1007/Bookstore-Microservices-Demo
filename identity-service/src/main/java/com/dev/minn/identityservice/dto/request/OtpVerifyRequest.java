package com.dev.minn.identityservice.dto.request;

import com.dev.minn.identityservice.annotation.EmailValid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OtpVerifyRequest {

    @EmailValid
    String email;

    @NotBlank
    String otp;
}
