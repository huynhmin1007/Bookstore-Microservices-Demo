package com.dev.minn.identityservice.event;

import lombok.Builder;

@Builder
public record SendRegistrationOtpEmailEvent(
        String email,
        String otp,
        long timeoutInSeconds
) {
}
