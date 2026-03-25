package com.dev.minn.identityservice.dto.request;

import com.dev.minn.identityservice.annotation.EmailValid;
import com.dev.minn.identityservice.annotation.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AuthenticationRequest {

    @EmailValid
    @NotBlank
    String email;

    @Password
    String password;
}
