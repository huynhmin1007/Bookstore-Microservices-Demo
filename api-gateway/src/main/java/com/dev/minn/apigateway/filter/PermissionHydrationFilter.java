package com.dev.minn.apigateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal; // Thêm cái này
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value; // Thêm cái này
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class PermissionHydrationFilter implements GlobalFilter, Ordered {

    ReactiveStringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("PermissionHydrationFilter");
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .flatMap(auth -> {
                    String scopeString = auth.getToken().getClaimAsString("scope");
                    String accountId = auth.getToken().getSubject();

                    if (!StringUtils.hasText(scopeString)) {
                        return chain.filter(exchange);
                    }

                    String[] roles = scopeString.split(" ");

                    return Flux.fromArray(roles)
                            .flatMap(roleName -> redisTemplate.opsForSet().members("role_permissions:" + roleName))
                            .collect(Collectors.toSet())
                            .flatMap(permissions -> {
                                String permsString = String.join(",", permissions);

                                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                        .headers(httpHeaders -> {
                                            // Dùng SET để ép buộc ghi đè (overwrite), triệt tiêu mọi header giả mạo từ Client
                                            httpHeaders.set("X-Account-Id", accountId);
                                            httpHeaders.set("X-User-Roles", scopeString);
                                            httpHeaders.set("X-User-Permissions", permsString);
                                        })
                                        .build();

                                ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

                                return chain.filter(mutatedExchange);
                            });
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}