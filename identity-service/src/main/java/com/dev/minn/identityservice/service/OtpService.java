package com.dev.minn.identityservice.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OtpService {

    RedisService redisService;
    ObjectMapper objectMapper;

    private static final String OTP_PREFIX = "otp:";

    public static String generateSecureOtp() {
        SecureRandom secureRandom = new SecureRandom();
        return String.valueOf(100000 + secureRandom.nextInt(900000));
    }

    public void cacheOtp(Action action, String email, String otp, long timeout, TimeUnit timeUnit) {
        redisService.set(buildOtpKey(action, email), otp, timeout, timeUnit);
    }

    public boolean verifyOtp(Action action, String email, String otp) {
        String cachedOtp = redisService.get(buildOtpKey(action, email), String.class);
        return cachedOtp != null && cachedOtp.equals(otp);
    }

    public boolean verifyAndClearOtp(Action action, String email, String otp) {
        boolean isVerified = verifyOtp(action, email, otp);
        if (isVerified) {
            redisService.delete(buildOtpKey(action, email));
        }
        return isVerified;
    }

    private String buildOtpKey(Action action, String email) {
        return OTP_PREFIX + action.name().toLowerCase() + ":" + email;
    }

    public boolean isOtpCached(String email, Action action) {
        return redisService.exists(buildOtpKey(action, email));
    }

    public enum Action {
        REGISTER, FORGOT_PASSWORD
    }
}
