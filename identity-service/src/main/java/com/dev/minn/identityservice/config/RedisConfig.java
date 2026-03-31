package com.dev.minn.identityservice.config;

import com.dev.minn.identityservice.entity.Permission;
import com.dev.minn.identityservice.entity.Role;
import com.dev.minn.identityservice.repository.PermissionRepository;
import com.dev.minn.identityservice.repository.RoleRepository;
import com.dev.minn.identityservice.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Notice the new Jackson 3 imports
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        // THE JACKSON 3 WAY: Chain all configurations in the Builder
        ObjectMapper redisObjectMapper = JsonMapper.builder()
                .findAndAddModules() // Automatically finds modules for Java 8 Dates (Instant, LocalDate)
                .activateDefaultTyping(validator, DefaultTyping.NON_FINAL)
                .build();

        GenericJacksonJsonRedisSerializer jsonSerializer = new GenericJacksonJsonRedisSerializer(redisObjectMapper);

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
            redisTemplate.getConnectionFactory().getConnection().flushDb(); // Xóa tất cả dữ liệu trong Redis
            System.out.println("Redis cache cleared on startup.");

            List<Role> roles = roleRepository.findAll();

            for(Role r : roles) {
                Set<String> permissions = permissionRepository.findAllPermissionByRole(r.getId())
                        .stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet());

                String redisKey = "role_permissions:ROLE_" + r.getName();

                if(!permissions.isEmpty())
                    redisTemplate.opsForSet()
                            .add(redisKey, permissions.toArray(new String[0]));
            }
        };
    }
}