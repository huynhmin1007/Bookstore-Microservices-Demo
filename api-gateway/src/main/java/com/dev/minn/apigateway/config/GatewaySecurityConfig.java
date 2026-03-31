package com.dev.minn.apigateway.config;

import com.dev.minn.apigateway.dto.ApiError;
import com.dev.minn.apigateway.exception.CodeException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.json.JsonParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class GatewaySecurityConfig {

    static final String[] PUBLIC_ENDPOINTS = {
            "/bookstore-api/identity/auth/**",
            "/public/**"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ServerAuthenticationEntryPoint entryPoint) { // 1. Tiêm EntryPoint vào đây thông qua DI

        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyExchange().authenticated())
                // 2. Xử lý lỗi khi KHÔNG CÓ token (Missing Bearer)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(entryPoint)
                )
                // 3. Xử lý lỗi khi TOKEN SAI/HẾT HẠN (OAuth2)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                        .authenticationEntryPoint(entryPoint)
                );

        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    @Bean
    public ServerAuthenticationEntryPoint jwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return (exchange, authException) -> {
            ServerHttpResponse response = exchange.getResponse();

            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            CodeException codeException = CodeException.UNAUTHENTICATED;
            ApiError apiError = ApiError.builder()
                    .code(codeException.getCode())
                    .message(codeException.getMessage())
                    .build();

            try {
                byte[] bytes = objectMapper.writeValueAsBytes(apiError);
                DataBuffer buffer = response.bufferFactory().wrap(bytes);

                return response.writeWith(Mono.just(buffer));
            } catch (JsonParseException e) {
                return Mono.error(e);
            }
        };
    }
}