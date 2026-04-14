package com.dev.minn.gateway.config;

import com.dev.minn.common.exception.CodeException;
import com.dev.minn.common.dto.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GatewaySecurityConfig {

    SecurityConfigProps props;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ServerAuthenticationEntryPoint entryPoint) {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(props.getPublicEndpoints().toArray(new String[0])).permitAll()
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
    public ServerAuthenticationEntryPoint jwtEntryPoint(ObjectMapper objectMapper) {
        return (exchange, authException) -> {
            ServerHttpResponse response = exchange.getResponse();

            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            CodeException codeException = CodeException.UNAUTHENTICATED;
            ApiResponse<?> apiError = ApiResponse.builder()
                    .code(codeException.getCode())
                    .message(codeException.getMessage())
                    .build();

            try {
                byte[] bytes = objectMapper.writeValueAsBytes(apiError);
                DataBuffer buffer = response.bufferFactory().wrap(bytes);

                return response.writeWith(Mono.just(buffer));
            } catch (JsonProcessingException e) {
                log.error("Error writing JSON response: ", e);
                return Mono.error(e);
            }
        };
    }
}
