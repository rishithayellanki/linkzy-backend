package com.practice.url_shortner.exception;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String identifier) {
        super("Rate limit exceeded for: " + identifier + ". Please try again later.");
    }
}
