package com.dev.minn.identityservice.dto.response;

import com.dev.minn.identityservice.constant.AccountStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AccountDetail {

    String id;
    String email;
    AccountStatus status;
    Instant createdAt;
    List<String> roles;
}
