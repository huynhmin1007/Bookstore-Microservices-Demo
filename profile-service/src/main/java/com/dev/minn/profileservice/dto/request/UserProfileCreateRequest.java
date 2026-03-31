package com.dev.minn.profileservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserProfileCreateRequest {

    @NotBlank(message = "user id must not be empty")
    String userId;

    @NotBlank(message = "first name must not be empty")
    String firstName;

    @NotBlank(message = "last name must not be empty")
    String lastName;
}
