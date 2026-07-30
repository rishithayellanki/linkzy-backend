package com.practice.url_shortner.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    // The 62 characters we use — ORDER MATTERS for encoding/decoding
    private static final String CHARACTERS =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;

    public String encode(long number) {
        if (number == 0) {
            return String.valueOf(CHARACTERS.charAt(0));
        }

        StringBuilder result = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % BASE);
            result.append(CHARACTERS.charAt(remainder));
            number = number / BASE;
        }

        // Reverse because we built it backwards
        return result.reverse().toString();
    }
}