package com.dev.minn.identityservice.client;

import com.dev.minn.identityservice.client.dto.UserProfileCreateRequest;
import com.dev.minn.identityservice.client.dto.UserProfileSummary;
import com.dev.minn.identityservice.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(
        name = "profile-service",
        url = "${client.profile-client}"
)
public interface ProfileClient {

    @PostMapping("/users")
    ApiResponse<UserProfileSummary>createProfile(
            @RequestHeader("X-Account-Id") String accountId,
            @RequestHeader("X-User-Roles") String roles,
            @RequestHeader("X-User-Permissions") String permissions,
            @Valid @RequestBody UserProfileCreateRequest request
    );
}
