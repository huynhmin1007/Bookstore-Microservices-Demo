package com.dev.minn.identityservice.listener;

import com.dev.minn.identityservice.config.RabbitMQConfigProps;
import com.dev.minn.identityservice.constant.AccountStatus;
import com.dev.minn.identityservice.event.ProfileSuccessEvent;
import com.dev.minn.identityservice.event.ProfileFailedEvent;
import com.dev.minn.identityservice.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class IdentityEventListener {

    AuthenticationService authenticationService;

    @RabbitListener(queues = "${app.rabbitmq.queue.profile-success}")
    public void handleProfileSuccess(ProfileSuccessEvent event) {
        authenticationService.changeStatus(UUID.fromString(event.accountId()), AccountStatus.ACTIVE);
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.profile-failed}")
    public void handleProfileFailed(ProfileFailedEvent event) {
        authenticationService.hardDelete(UUID.fromString(event.accountId()));
        log.error("Profile creation failed for accountId: {}. Account has been deleted. Error message: {}"
                , event.accountId(),
                event.errorMessage());
    }
}
