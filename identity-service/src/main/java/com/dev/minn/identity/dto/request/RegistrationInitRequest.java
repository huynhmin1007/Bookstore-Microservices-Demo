package com.dev.minn.identity.dto.request;

import com.dev.minn.common.annotation.EmailValid;
import com.dev.minn.common.annotation.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RegistrationInitRequest {

    @EmailValid
    @NotBlank
    String email;

    @Password
    String password;

    @NotBlank(message = "first name must not be empty")
    String firstName;

    @NotBlank(message = "last name must not be empty")
    String lastName;
}
