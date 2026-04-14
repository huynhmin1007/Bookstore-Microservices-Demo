package com.dev.minn.identity.dto.response;

import com.dev.minn.identity.constant.AccountStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountResponse {

    String id;
    String email;
    String firstName;
    String lastName;
    AccountStatus status;
    Instant createdAt;
    List<String> roles;
}
