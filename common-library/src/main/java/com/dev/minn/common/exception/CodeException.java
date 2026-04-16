package com.dev.minn.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum CodeException {

    INTERNAL_ERROR(5000, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(4000, "Invalid Request Parameters", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(1001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1002, "Access denied", HttpStatus.FORBIDDEN),

    TOKEN_EXPIRED(1005, "Token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_REVOKED(1006, "Token has been revoked", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(1007, "Token is invalid", HttpStatus.UNAUTHORIZED),

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
    INVALID_CREDENTIALS(2010, "Invalid credentials", HttpStatus.UNAUTHORIZED),
    PROFILE_CREATION_FAILED(2011, "Failed to create profile", HttpStatus.INTERNAL_SERVER_ERROR),
    RESOURCE_NOT_FOUND(2012, "Resource not found", HttpStatus.NOT_FOUND),
    BOOK_NOT_FOUND(2013, "Book not found", HttpStatus.NOT_FOUND),

    // 3xxx — Role & Permission
    ROLE_NOT_FOUND(3001, "Role not found", HttpStatus.NOT_FOUND),
    ROLE_ALREADY_EXISTS(3002, "Role already exists", HttpStatus.CONFLICT),
    PERMISSION_NOT_FOUND(3003, "Permission not found", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus status;

    public AppException throwException() {
        return new AppException(this);
    }
}
