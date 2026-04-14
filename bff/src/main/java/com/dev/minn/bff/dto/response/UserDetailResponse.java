package com.dev.minn.bff.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class UserDetailResponse {
    String accountId;
    String profileId;
    String email;
    String firstName;
    String lastName;
}
