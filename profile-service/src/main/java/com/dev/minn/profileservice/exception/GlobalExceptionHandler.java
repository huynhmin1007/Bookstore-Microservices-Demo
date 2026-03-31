package com.dev.minn.profileservice.exception;

import com.dev.minn.profileservice.dto.ApiError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiError> handleAppException(AppException e) {
        CodeException code = e.getCodeException();
        return ResponseEntity
                .status(code.getStatus())
                .body(ApiError.builder()
                        .code(code.getCode())
                        .message(code.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity
                .badRequest()
                .body(ApiError.builder()
                        .code(CodeException.INVALID_REQUEST.getCode())
                        .message(CodeException.INVALID_REQUEST.getMessage())
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception e) {
        return ResponseEntity
                .internalServerError()
                .body(ApiError.builder()
                        .code(CodeException.INTERNAL_ERROR.getCode())
                        .message(CodeException.INTERNAL_ERROR.getMessage())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException e) {
        CodeException codeException = CodeException.UNAUTHORIZED;

        return ResponseEntity
                .status(codeException.getStatus())
                .body(ApiError.builder()
                        .code(codeException.getCode())
                        .message(codeException.getMessage())
                        .build());
    }
}
