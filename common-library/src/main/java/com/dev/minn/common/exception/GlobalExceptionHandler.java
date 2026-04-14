package com.dev.minn.common.exception;

import com.dev.minn.common.dto.response.ApiResponse;
import com.dev.minn.common.utils.MDCHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.dev.minn")
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException e) {
        CodeException code = e.getCodeException();
        log.warn("AppException: {} - {}", code.getCode(), code.getMessage());

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.<Void>builder()
                        .code(code.getCode())
                        .message(code.getMessage())
                        .traceId(MDCHelper.getTraceId())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("Validation Error: {}", errors);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.<Map<String, String>>builder()
                        .code(CodeException.INVALID_REQUEST.getCode())
                        .message(CodeException.INVALID_REQUEST.getMessage())
                        .data(errors)
                        .traceId(MDCHelper.getTraceId())
                        .build()
                );
    }

    // 3. Bắt các lỗi vỡ hệ thống (NullPointer, đứt mạng...)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception e) {
        log.error("Unhandled Exception: ", e);

        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.<Void>builder()
                        .code(CodeException.INTERNAL_ERROR.getCode())
                        .message(CodeException.INTERNAL_ERROR.getMessage())
                        .traceId(MDCHelper.getTraceId())
                        .build());
    }
}