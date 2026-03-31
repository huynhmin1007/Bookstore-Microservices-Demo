package com.dev.minn.apigateway.exception;

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
    TOKEN_REVOKED(1006, "Token has been revoked", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID(1006, "Refresh token is invalid or expired", HttpStatus.UNAUTHORIZED),

    // 2xxx — User domain

    // 3xxx — Role & Permission
    ROLE_NOT_FOUND(3001, "Role not found", HttpStatus.NOT_FOUND),
    ROLE_ALREADY_EXISTS(3002, "Role already exists", HttpStatus.CONFLICT),
    PERMISSION_NOT_FOUND(3003, "Permission not found", HttpStatus.NOT_FOUND),

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
