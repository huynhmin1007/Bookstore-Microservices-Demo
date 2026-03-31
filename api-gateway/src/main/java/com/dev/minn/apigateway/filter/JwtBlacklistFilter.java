package com.dev.minn.apigateway.filter;

import com.dev.minn.apigateway.exception.AppException;
import com.dev.minn.apigateway.exception.CodeException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JwtBlacklistFilter implements GlobalFilter, Ordered {

    ReactiveStringRedisTemplate redisTemplate;

    final static String BLACKLIST_PREFIX = "blacklist:jti:";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("JwtBlacklistFilter");
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(auth -> auth.getToken().getId())
                .flatMap(jti -> redisTemplate.hasKey(BLACKLIST_PREFIX + jti))
                .flatMap(isRevoked -> {
                    if (Boolean.TRUE.equals(isRevoked)) {
                        log.warn("Access Denied: Token đã bị thu hồi.");
                        return Mono.error(new AppException(CodeException.TOKEN_EXPIRED));
                    }
                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
