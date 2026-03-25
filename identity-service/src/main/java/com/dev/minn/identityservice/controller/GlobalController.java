package com.dev.minn.identityservice.controller;

import com.dev.minn.identityservice.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/public")
public class GlobalController {

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Void>> connect() {
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Connected")
                .build());
    }
}
