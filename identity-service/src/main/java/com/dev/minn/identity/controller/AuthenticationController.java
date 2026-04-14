package com.dev.minn.identity.controller;

import com.dev.minn.common.dto.response.ApiResponse;
import com.dev.minn.identity.dto.request.AuthenticationRequest;
import com.dev.minn.identity.dto.request.LogoutRequest;
import com.dev.minn.identity.dto.request.RegistrationInitRequest;
import com.dev.minn.identity.dto.request.RegistrationVerifyRequest;
import com.dev.minn.identity.dto.response.AccountResponse;
import com.dev.minn.identity.dto.response.AuthenticationResponse;
import com.dev.minn.identity.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .data(authenticationService.authenticate(request))
                .message("Authentication successful")
                .build();
    }

    @PostMapping("/logout")
    public String logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        authenticationService.logout(request);
        return "Logged out successfully";
    }

    @PostMapping("/register")
    public String initiateRegistration(
            @Valid @RequestBody RegistrationInitRequest request
    ) {
        authenticationService.initiateRegistration(request);
        return "Otp sent to email, please check your email";
    }

    @PostMapping("/register/verify-otp")
    public ApiResponse<AccountResponse> confirmRegistration(
            @Valid @RequestBody RegistrationVerifyRequest request
    ) {
        return ApiResponse.<AccountResponse>builder()
                .data(authenticationService.confirmRegistration(request))
                .message("Registration successful")
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@iam.check('identity:account:delete') or #id == authentication.name")
    public String deleteAccount(
            @PathVariable String id
    ) {
        authenticationService.softDelete(UUID.fromString(id));

        return "Account deleted successfully";
    }
}
