package com.dev.minn.identityservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Builder
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@ToString
public class UserProfileSummary {

    String id;
    String userId;
    String firstName;
    String lastName;
}
