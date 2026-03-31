package com.dev.minn.profileservice.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final CodeException codeException;

    public AppException(CodeException codeException) {
        super(codeException.getMessage());
        this.codeException = codeException;
    }
}
