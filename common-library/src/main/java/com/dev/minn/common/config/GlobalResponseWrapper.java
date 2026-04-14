package com.dev.minn.common.config;

import com.dev.minn.common.dto.response.ApiResponse;
import com.dev.minn.common.utils.MDCHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Instant;

@RestControllerAdvice(basePackages = "com.dev.minn")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalResponseWrapper implements ResponseBodyAdvice<Object> {

    ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        String methodName = returnType.getMethod().getName();

        if (methodName.equals("keys")) {
            return false;
        }

        return true;
    }

    @SneakyThrows
    @Override
    public @Nullable Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof String) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .message((String) body)
                    .timestamp(Instant.now())
                    .traceId(MDCHelper.getTraceId())
                    .build();

            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return objectMapper.writeValueAsString(apiResponse);
        }

        if (body instanceof ApiResponse<?> apiResponse) {
            if (apiResponse.getTraceId() == null)
                apiResponse.setTraceId(MDCHelper.getTraceId());

            return apiResponse;
        }

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .data(body)
                .traceId(MDCHelper.getTraceId())
                .build();

        return apiResponse;
    }
}
