package com.practice.url_shortner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    // ↑ Spring Boot auto-configures this bean for you
    //   because of the Redis dependency + connection properties

    // SET a value (like Redis CLI's SET command)
    public void saveValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // SET with expiry (like SET key value EX seconds)
    public void saveValueWithExpiry(String key, String value, long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    // GET a value (like Redis CLI's GET command)
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
        // Returns null if key doesn't exist — just like in CLI
    }

    // DELETE a value
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    // Check if key exists
    public boolean keyExists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
