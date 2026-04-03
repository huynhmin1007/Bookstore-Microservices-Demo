package com.dev.minn.identityservice.controller;

import com.dev.minn.identityservice.dto.ApiResponse;
import com.dev.minn.identityservice.dto.request.AuthenticationRequest;
import com.dev.minn.identityservice.dto.request.LogoutRequest;
import com.dev.minn.identityservice.dto.request.RegistrationInitRequest;
import com.dev.minn.identityservice.dto.request.RegistrationVerifyRequest;
import com.dev.minn.identityservice.dto.response.AccountSummary;
import com.dev.minn.identityservice.dto.response.AuthenticationResponse;
import com.dev.minn.identityservice.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(ApiResponse.<AuthenticationResponse>builder()
                .data(authenticationService.authenticate(request))
                .message("Authentication successful")
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        authenticationService.logout(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Logout successful")
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> initiateRegistration(
            @Valid @RequestBody RegistrationInitRequest request
    ) {
        authenticationService.initiateRegistration(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Otp sent to email, please check your email")
                .build());
    }

    @PostMapping("/register/verify-otp")
    public ResponseEntity<ApiResponse<AccountSummary>> confirmRegistration(
            @Valid @RequestBody RegistrationVerifyRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.<AccountSummary>builder()
                .data(authenticationService.confirmRegistration(request))
                .message("Registration successful. Profile is initializing. Please wait...")
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@iam.check('identity:account:delete') or #id == authentication.name")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @PathVariable String id
    ) {
        authenticationService.softDelete(UUID.fromString(id));

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Account deleted successfully")
                .build());
    }
}
