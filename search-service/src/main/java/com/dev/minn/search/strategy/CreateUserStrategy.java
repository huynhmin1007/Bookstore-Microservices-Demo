package com.dev.minn.search.strategy;

import com.dev.minn.common.strategy.EventHandlerStrategy;
import com.dev.minn.search.document.User;
import com.dev.minn.search.event.UserCreatedEvent;
import com.dev.minn.search.mapper.UserMapper;
import com.dev.minn.search.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CreateUserStrategy implements EventHandlerStrategy {

    UserRepository userRepository;
    UserMapper userMapper;
    ObjectMapper objectMapper;

    @Override
    public boolean supports(String eventType) {
        return "USER_CREATED".equalsIgnoreCase(eventType);
    }

    @Override
    public void handle(JsonNode payload) throws Exception {
        UserCreatedEvent event = objectMapper.treeToValue(payload, UserCreatedEvent.class);
        User user = userMapper.toUser(event);
        userRepository.save(user);
        log.info("User created: {}", user);
    }

    @Override
    public void handle(JsonNode payload, String eventType) throws Exception {

    }
}
