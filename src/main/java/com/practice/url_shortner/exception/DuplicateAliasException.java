package com.practice.url_shortner.exception;

public class DuplicateAliasException extends RuntimeException {
    public DuplicateAliasException(String alias) {
        super("Custom alias is already taken: " + alias);
    }
}