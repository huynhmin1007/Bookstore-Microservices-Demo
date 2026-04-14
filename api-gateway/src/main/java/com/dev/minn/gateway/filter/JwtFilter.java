package com.dev.minn.gateway.filter;

import com.dev.minn.common.exception.AppException;
import com.dev.minn.common.exception.CodeException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JwtFilter implements GlobalFilter, Ordered {

    ReactiveStringRedisTemplate redisTemplate;

    @Value("${app.security.filters[0].blacklist-order:-1}")
    @NonFinal
    int order;

    private static final String BLACKLIST_PREFIX = "blacklist:jti:";
    private static final String BANNED_SESSION_PREFIX = "banned_session:";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken)
                .flatMap(jwt -> validateTokenWithRedis(jwt))
                .flatMap(isValid -> {
                    if (Boolean.FALSE.equals(isValid)) {
                        log.warn("Access denied: Token is revoked or session is banned");
                        return Mono.error(new AppException(CodeException.TOKEN_REVOKED));
                    }
                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<Boolean> validateTokenWithRedis(Jwt jwt) {
        String jti = jwt.getId();
        String accountId = jwt.getSubject();
        Instant issueTime = jwt.getIssuedAt();

        Mono<Boolean> isBlacklistedMono = redisTemplate.hasKey(BLACKLIST_PREFIX + jti);

        Mono<Boolean> isBannedMono = redisTemplate.opsForValue().get(BANNED_SESSION_PREFIX + accountId)
                .map(bannedEpochStr -> {
                    if (issueTime != null) {
                        long bannedEpoch = Long.parseLong(bannedEpochStr);
                        long tokenEpoch = issueTime.toEpochMilli();
                        return tokenEpoch <= bannedEpoch;
                    }
                    return false;
                })
                .defaultIfEmpty(false);

        return Mono.zip(isBlacklistedMono, isBannedMono)
                .map(tuple -> {
                    boolean isBlacklisted = tuple.getT1();
                    boolean isBanned = tuple.getT2();
                    return !isBlacklisted && !isBanned;
                });
    }

    @Override
    public int getOrder() {
        return order;
    }
}