package com.dev.minn.profileservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CodeException {

    // 1xxx — Authentication & Authorization
    UNAUTHENTICATED(1001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1002, "Access denied", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(1003, "Invalid username or password", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(1004, "Token is invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(1005, "Token has expired", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID(1006, "Refresh token is invalid or expired", HttpStatus.UNAUTHORIZED),

    // 2xxx — User domain

    // 3xxx — Role & Permission

    // 4xxx — Validation & Request
    INVALID_REQUEST(4001, "Invalid request", HttpStatus.BAD_REQUEST),

    // 9xxx — System
    INTERNAL_ERROR(9999, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;

    public AppException throwException() {
        return new AppException(this);
    }
}
