package com.dev.minn.identity.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PendingAccountInfo {
    String email;
    String hashedPassword;
    String firstName;
    String lastName;
}
