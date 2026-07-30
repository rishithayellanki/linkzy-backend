package com.practice.url_shortner.repository;

import com.practice.url_shortner.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    // Find single token by token string — used for password reset
    Optional<PasswordResetToken> findByToken(String token);

    // Delete all tokens for an email — used before creating new token
    void deleteByEmail(String email);

    // Find all tokens for an email — safer deletion approach
    List<PasswordResetToken> findAllByEmail(String email);
}