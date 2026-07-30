package com.practice.url_shortner.exception;

public class UrlNotFoundException extends RuntimeException {

    public UrlNotFoundException(String shortCode) {
        super("URL not found for short code: " + shortCode);
    }
}