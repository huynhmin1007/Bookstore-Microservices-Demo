package com.dev.minn.identity.config;

import com.dev.minn.identity.entity.Permission;
import com.dev.minn.identity.entity.Role;
import com.dev.minn.identity.repository.PermissionRepository;
import com.dev.minn.identity.repository.RoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisConfig {

    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        PolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();

        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .activateDefaultTyping(validator, DefaultTyping.NON_FINAL)
                .build();

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public ApplicationRunner clearRedisOnStartup(RedisTemplate<String, Object> redisTemplate) {
        return args -> {
            redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
            log.info("Redis cache cleared on startup.");

            List<Role> roles = roleRepository.findAll();

            for(Role r : roles) {
                Set<String> permissions = permissionRepository.findAllPermissionByRole(r.getId())
                        .stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet());

                String redisKey = "role_permissions:ROLE_" + r.getName();

                if(!permissions.isEmpty()) {
                    redisTemplate.opsForSet().add(redisKey, permissions.toArray(new String[0]));
                    log.info("Loaded {} permissions for role {}", permissions.size(), r.getName());
                }
            }
        };
    }
}