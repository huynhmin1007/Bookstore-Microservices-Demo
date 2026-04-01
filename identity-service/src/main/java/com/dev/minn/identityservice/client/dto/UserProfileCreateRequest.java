package com.dev.minn.identityservice.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Builder
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
