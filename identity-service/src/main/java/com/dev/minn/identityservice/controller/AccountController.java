package com.dev.minn.identityservice.controller;

import com.dev.minn.identityservice.dto.ApiResponse;
import com.dev.minn.identityservice.dto.request.AccountSearchRequest;
import com.dev.minn.identityservice.dto.response.AccountDetail;
import com.dev.minn.identityservice.dto.response.AccountSummary;
import com.dev.minn.identityservice.dto.response.PageResponse;
import com.dev.minn.identityservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AccountController {

    AccountService accountService;

    @GetMapping("/{id}")
    @PreAuthorize("@iam.check('identity:account:read') or #id == authentication.name")
    public ResponseEntity<ApiResponse<AccountDetail>> getDetailAccount(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(ApiResponse.<AccountDetail>builder()
                .data(accountService.getDetailAccount(UUID.fromString(id)))
                .build());
    }

    @GetMapping
    @PreAuthorize("@iam.check('identity:account:read-all')")
    public ResponseEntity<ApiResponse<PageResponse<AccountSummary>>> searchAccounts(
            @Valid @ModelAttribute AccountSearchRequest request
    ) {
        Page<AccountSummary> page = accountService.searchAccounts(request);
        return ResponseEntity.ok(ApiResponse.<PageResponse<AccountSummary>>builder()
                .data(PageResponse.of(page))
                .build());
    }
}
