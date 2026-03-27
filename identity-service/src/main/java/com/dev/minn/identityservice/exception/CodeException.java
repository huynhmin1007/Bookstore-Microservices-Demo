package com.dev.minn.identityservice.exception;

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
    USER_NOT_FOUND(2001, "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(2002, "User already exists", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS(2003, "Email already exists", HttpStatus.CONFLICT),
    ACCOUNT_DISABLED(2004, "Account is disabled", HttpStatus.FORBIDDEN),
    ACCOUNT_LOCKED(2005, "Account is locked", HttpStatus.FORBIDDEN),
    OTP_INVALID(2006, "OTP is invalid or expired", HttpStatus.UNAUTHORIZED),
    OTP_EXPIRED(2007, "OTP has expired", HttpStatus.UNAUTHORIZED),
    REGISTRATION_PENDING(2008, "Registration already pending verification", HttpStatus.CONFLICT),
    ACCOUNT_DELETED(2009, "Account has been deleted", HttpStatus.GONE),

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
