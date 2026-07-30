package com.practice.url_shortner.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;
    // ↑ reads from application.properties automatically

    @Value("${jwt.expiration}")
    private long expiration;

    // Generate a JWT token for a user
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)          // who this token is for
                .issuedAt(new Date())    // when it was created
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())  // sign with secret key
                .compact();
    }

    // Extract email from a token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // Check if token is still valid (not expired)
    public boolean isTokenValid(String token) {
        try {
            Date expiry = extractClaims(token).getExpiration();
            return expiry.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // Parse the token and get its contents
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Convert the secret string into a proper cryptographic key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}