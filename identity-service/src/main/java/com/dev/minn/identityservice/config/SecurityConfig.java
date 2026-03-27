package com.dev.minn.identityservice.config;

import com.dev.minn.identityservice.dto.ApiError;
import com.dev.minn.identityservice.exception.AppException;
import com.dev.minn.identityservice.exception.CodeException;
import com.dev.minn.identityservice.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {

    RsaKeyConfig rsaKeys;
    JwtService jwtService;

    private final String[] PUBLIC_ENDPOINTS = {
            "/auth/**",
            "/public/**"
    };

    private final String[] SECURED_ENDPOINTS = {
            "/auth/logout"
    };

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(request -> request
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers(SECURED_ENDPOINTS).authenticated()
                .anyRequest().authenticated()
        );

        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
                .authenticationEntryPoint(jwtAuthenticationEntryPoint()));

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("scope");
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return converter;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();

        OAuth2TokenValidator<Jwt> defaultValidator = new JwtTimestampValidator();

        OAuth2TokenValidator<Jwt> customValidator = new DelegatingOAuth2TokenValidator<>(
                defaultValidator,
                this::customValidator
        );

        jwtDecoder.setJwtValidator(customValidator);
        return jwtDecoder;
    }

    private OAuth2TokenValidatorResult customValidator(Jwt jwt) {
        String jti = jwt.getId();
        String tokenType = jwt.getClaimAsString("tokenType");

        boolean isValid = jwtService.validateBusinessRules(jti, tokenType, JwtService.TokenType.ACCESS);

        if (!isValid) {
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Token sai loại hoặc đã bị thu hồi", null)
            );
        }

        return OAuth2TokenValidatorResult.success();
    }

    @Bean
    AuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            CodeException codeException = CodeException.UNAUTHENTICATED;

            ApiError apiError = ApiError.builder()
                    .code(codeException.getCode())
                    .message(codeException.getMessage())
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(apiError));
            response.flushBuffer();
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
