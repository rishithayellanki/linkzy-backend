package com.practice.url_shortner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final int MAX_REQUESTS = 5;
    private static final long WINDOW_SECONDS = 60;

    public void checkRateLimit(String identifier) {
        String key = "rate_limit:" + identifier;

        String currentCountStr = redisTemplate.opsForValue().get(key);

        if (currentCountStr == null) {
            // First request from this identifier — start counting
            redisTemplate.opsForValue().set(key, "1", WINDOW_SECONDS, TimeUnit.SECONDS);
            System.out.println("Rate limit: " + identifier + " — request 1/" + MAX_REQUESTS);
            return;
        }

        int currentCount = Integer.parseInt(currentCountStr);

        if (currentCount >= MAX_REQUESTS) {
            // Already at/over the limit — BLOCK this request
            System.out.println("Rate limit EXCEEDED for: " + identifier);
            throw new com.practice.url_shortner.exception.RateLimitExceededException(identifier);
        }

        // Increment the counter, but KEEP the existing expiry
        // (using Redis's built-in INCR-style operation)
        redisTemplate.opsForValue().increment(key);
        System.out.println("Rate limit: " + identifier + " — request " + (currentCount + 1) + "/" + MAX_REQUESTS);
    }
}