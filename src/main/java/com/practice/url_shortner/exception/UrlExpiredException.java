package com.practice.url_shortner.exception;

public class UrlExpiredException extends RuntimeException {
    public UrlExpiredException(String shortCode) {
        super("URL has expired and is no longer accessible: " + shortCode);
    }
}