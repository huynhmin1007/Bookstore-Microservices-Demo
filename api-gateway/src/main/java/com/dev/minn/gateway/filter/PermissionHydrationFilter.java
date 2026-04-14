package com.dev.minn.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionHydrationFilter implements GlobalFilter, Ordered {

    @Value("${app.security.filters[1].permissions-order}")
    @NonFinal
    int order;

    ReactiveStringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .flatMap(auth -> {
                    var token = auth.getToken();
                    String scope = token.getClaimAsString("scope");
                    String accountId = token.getSubject();

                    if (!StringUtils.hasText(scope)) {
                        return chain.filter(exchange);
                    }

                    String[] roles = scope.split(" ");

                    return Flux.fromArray(roles)
                            .flatMap(role -> redisTemplate.opsForSet().members("role_permissions:" + role))
                            .collect(Collectors.toSet())
                            .flatMap(permissions -> {
                                String permString = String.join(",", permissions);

                                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                        .headers(httpHeader -> {
                                            httpHeader.set("X-Account-Id", accountId);
                                            httpHeader.set("X-User-Roles", scope);
                                            httpHeader.set("X-User-Permissions", permString);
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
        return order;
    }
}
