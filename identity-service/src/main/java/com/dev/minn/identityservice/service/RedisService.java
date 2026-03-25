package com.dev.minn.identityservice.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisService {

    RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public <T> T get(String key, Class<T> clazz) {
        Object result = redisTemplate.opsForValue().get(key);
        return result != null ? clazz.cast(result) : null;
    }

    public <T> T getAndClear(String key, Class<T> clazz) {
        T result = get(key, clazz);
        if( result != null)
            delete(key);
        return result;
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void hashSet(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public <T> T hashGet(String key, String field, Class<T> clazz) {
        Object result = redisTemplate.opsForHash().get(key, field);
        return result != null ? clazz.cast(result) : null;
    }

    public Map<Object, Object> hashGetAll(String hashKey) {
        return redisTemplate.opsForHash().entries(hashKey);
    }
}
