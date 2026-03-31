package com.dev.minn.profileservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Builder
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserProfileSummary {

    String id;
    String userId;
    String firstName;
    String lastName;
}
