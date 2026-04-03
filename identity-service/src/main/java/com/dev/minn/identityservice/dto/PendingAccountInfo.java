package com.dev.minn.identityservice.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PendingAccountInfo {
    String email;
    String hashedPassword;
    String firstName;
    String lastName;
}
