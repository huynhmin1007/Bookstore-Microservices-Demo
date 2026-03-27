package com.dev.minn.identityservice.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AccountSearchRequest extends SearchRequest {

    String role;

    @Pattern(regexp = "(?i)^(ACTIVE|INACTIVE|BANNED|DELETED)$", message = "Status invalid")
    String status;
}
