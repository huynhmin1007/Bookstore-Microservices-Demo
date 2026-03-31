package com.dev.minn.profileservice.controller;

import com.dev.minn.profileservice.dto.ApiResponse;
import com.dev.minn.profileservice.dto.request.UserProfileCreateRequest;
import com.dev.minn.profileservice.dto.response.UserProfileSummary;
import com.dev.minn.profileservice.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {

    UserProfileService userProfileService;

    @PostMapping()
    @PreAuthorize("@iam.check('profile:user:create')")
    public ResponseEntity<ApiResponse<UserProfileSummary>> createUserProfile(
            @Valid @RequestBody UserProfileCreateRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.<UserProfileSummary>builder()
                    .message("User profile created")
                    .data(userProfileService.createUserProfile(request))
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
