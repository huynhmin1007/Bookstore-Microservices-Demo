package com.dev.minn.profileservice.listener;

import com.dev.minn.profileservice.config.RabbitMQConfig;
import com.dev.minn.profileservice.config.RabbitMQConfigProps;
import com.dev.minn.profileservice.dto.request.UserProfileCreateRequest;
import com.dev.minn.profileservice.event.AccountCreatedEvent;
import com.dev.minn.profileservice.event.ProfileFailedEvent;
import com.dev.minn.profileservice.event.ProfileSuccessEvent;
import com.dev.minn.profileservice.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ProfileEventListener {

    RabbitMQConfigProps props;

    UserProfileService userProfileService;
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "${app.rabbitmq.queue.profile-created}")
    public void handleAccountCreatedEvent(AccountCreatedEvent event) {
        try {
//            userProfileService.createUserProfile(UserProfileCreateRequest.builder()
//                    .userId(event.accountId())
//                    .email(event.email())
//                    .firstName(event.firstName())
//                    .lastName(event.lastName())
//                    .build());
//
//            rabbitTemplate.convertAndSend(
//                    props.getExchange().getIdentity(),
//                    props.getRoutingKey().getProfileSuccess(),
//                    new ProfileSuccessEvent(
//                            event.accountId()
//                    )
//            );
            throw new RuntimeException("Test");
        } catch (Exception e) {
            rabbitTemplate.convertAndSend(
                    props.getExchange().getIdentity(),
                    props.getRoutingKey().getProfileFailed(),
                    new ProfileFailedEvent(
                            event.accountId(),
                            e.getMessage()
                    )
            );
            e.printStackTrace();
        }
    }
}
