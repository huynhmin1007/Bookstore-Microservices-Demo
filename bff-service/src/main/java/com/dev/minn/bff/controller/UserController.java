package com.dev.minn.bff.controller;

import com.dev.minn.bff.dto.response.UserDetailResponse;
import com.dev.minn.bff.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

    UserService userService;

    @GetMapping("/{accountId}")
    @PreAuthorize("@iam.check('identity:account:read') or #accountId == authentication.name")
    public UserDetailResponse getUserDetail(
            @PathVariable("accountId") String accountId
    ) {
        log.info("Getting user detail for accountId: {}", accountId);
        return userService.getUserDetail(accountId);
    }
}
