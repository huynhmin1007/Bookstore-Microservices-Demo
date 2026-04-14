package com.dev.minn.identity.dto.request;

import com.dev.minn.common.annotation.EmailValid;
import com.dev.minn.common.annotation.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {

    @EmailValid
    @NotBlank
    String email;

    @Password
    String password;
}
