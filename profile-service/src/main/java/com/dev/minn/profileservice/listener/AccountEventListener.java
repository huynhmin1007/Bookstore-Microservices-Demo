package com.dev.minn.profileservice.listener;

import com.dev.minn.profileservice.dto.request.UserProfileCreateRequest;
import com.dev.minn.profileservice.event.AccountCreatedEvent;
import com.dev.minn.profileservice.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountEventListener {

    UserProfileService userProfileService;

    @RabbitListener(queues = "profile.account.created.queue")
    public void handleAccountCreatedEvent(AccountCreatedEvent event) {
        log.info("Received AccountCreatedEvent: {}", event);

        try {
            userProfileService.createUserProfile(UserProfileCreateRequest.builder()
                            .userId(event.accountId())
                            .email(event.email())
                            .firstName(event.firstName())
                            .lastName(event.lastName())
                    .build());
            log.info("User profile created successfully for userId: {}", event.accountId());
        } catch (Exception e) {
            log.error("Failed to create user profile for userId: {}. Error: {}", event.accountId(), e.getMessage());
        }
    }
}
