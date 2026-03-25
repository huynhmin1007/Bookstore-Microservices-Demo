package com.dev.minn.identityservice.listener;

import com.dev.minn.identityservice.event.SendRegistrationOtpEmailEvent;
import com.dev.minn.identityservice.event.SendWelcomeEmailEvent;
import com.dev.minn.identityservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailNotificationListener {

    EmailService emailService;

    @Async("notificationExecutor")
    @EventListener
    public void handleSendOtpEmailEvent(SendRegistrationOtpEmailEvent event) {
        Map<String, String> placeholders = Map.of(
                "otp", event.otp(),
                "timeout", String.valueOf(event.timeoutInSeconds() / 60)
        );
        emailService.sendTemplatedEmail(event.email(), "Mã xác nhận đăng ký tài khoản", "otp", placeholders);
    }

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSendWelcomeEmailEvent(SendWelcomeEmailEvent event) {
        Map<String, String> placeholders = Map.of(
                "email", event.email()
        );
        emailService.sendTemplatedEmail(event.email(), "Chào mừng bạn đến với Bookstore!", "welcome", placeholders);
    }
}
