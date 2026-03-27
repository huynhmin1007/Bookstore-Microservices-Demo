package com.dev.minn.identityservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    int code;
    String message;
    Map<String, String> errors;
}
