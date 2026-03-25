package com.dev.minn.identityservice.event;

public record SendRegistrationOtpEmailEvent(
        String email,
        String otp,
        long timeoutInSeconds
) {
}
