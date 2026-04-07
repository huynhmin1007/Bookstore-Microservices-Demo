package com.dev.minn.notificationservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HeaderAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accountId = request.getHeader("X-Account-Id");
        String rolesHeader = request.getHeader("X-User-Roles");
        String permissionsHeader = request.getHeader("X-User-Permissions");

        if(StringUtils.hasText(accountId)) {
            List<GrantedAuthority> authorities = new ArrayList<>();

            if(StringUtils.hasText(rolesHeader)) {
                String cleanRoles = rolesHeader.replace("\"", "");
                authorities.addAll(Arrays.stream(cleanRoles.split(" "))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet()));
            }

            // Xử lý Permissions: Xoá dấu nháy kép và khoảng trắng thừa trước khi split
            if(StringUtils.hasText(permissionsHeader)) {
                String cleanPermissions = permissionsHeader.replace("\"", "").replace(" ", "");
                authorities.addAll(Arrays.stream(cleanPermissions.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet()));
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    accountId,
                    null,
                    authorities
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
