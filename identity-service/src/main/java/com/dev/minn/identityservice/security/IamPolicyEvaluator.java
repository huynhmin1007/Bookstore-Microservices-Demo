package com.dev.minn.identityservice.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component("iam")
@Slf4j
public class IamPolicyEvaluator {

    public boolean check(String requiredPermission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return false;
        }

        // Extract the user's policies from the Security Context (populated by JWT)
        Set<String> userPolicies = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        for (String policy : userPolicies) {
            // 1. Check for absolute root access
            if (policy.equals("*:*:*") || policy.equals("*")) {
                return true;
            }

            // 2. Convert AWS wildcard syntax to Java Regex
            // Example: "identity:account:*" becomes "^identity:account:.*$"
            String regexPattern = "^" + policy.replace("*", ".*") + "$";

            if (requiredPermission.matches(regexPattern)) {
                log.debug("Access Granted: Required [{}] matched Policy [{}]", requiredPermission, policy);
                return true;
            }
        }

        log.warn("Access Denied: User lacks permission [{}]", requiredPermission);
        return false;
    }
}