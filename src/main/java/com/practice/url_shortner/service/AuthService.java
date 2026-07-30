package com.practice.url_shortner.service;

import com.practice.url_shortner.dto.AuthResponse;
import com.practice.url_shortner.dto.LoginRequest;
import com.practice.url_shortner.dto.RegisterRequest;
import com.practice.url_shortner.exception.EmailAlreadyExistsException;
import com.practice.url_shortner.exception.InvalidCredentialsException;
import com.practice.url_shortner.model.User;
import com.practice.url_shortner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.practice.url_shortner.model.PasswordResetToken;
import com.practice.url_shortner.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import jakarta.mail.MessagingException;
import java.util.UUID;
import java.util.List;
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    // ↑ BCryptPasswordEncoder from SecurityConfig
    //   Spring injects it automatically via @Autowired
    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    public AuthResponse register(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // Hash the password before saving
        // NEVER store plain text passwords!
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create and save the user
        User user = new User(
                request.getEmail(),
                hashedPassword,
                request.getName()
        );
        userRepository.save(user);

        // Generate JWT token immediately after registration
        // So user doesn't need to log in separately after registering
        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getName(),
                "Registration successful!"
        );
    }

    public AuthResponse login(LoginRequest request) {

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);
        // ↑ don't reveal "email not found" — say "invalid credentials"
        //   this prevents attackers from knowing which emails are registered

        // Verify password against the stored hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        // ↑ passwordEncoder.matches() compares:
        //   plain text ("mypassword") vs stored hash ("$2a$10$...")
        //   BCrypt handles this comparison securely

        // Generate JWT token
        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getName(),
                "Login successful!"
        );
    }
    @Transactional
    public void forgotPassword(String email) {
        // Check if user exists
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "No account found with email: " + email
                ));

        // Find and delete existing tokens one by one (safer than deleteByEmail)
        List<PasswordResetToken> existingTokens =
                tokenRepository.findAllByEmail(email);
        if (!existingTokens.isEmpty()) {
            tokenRepository.deleteAll(existingTokens);
        }

        // Generate new secure token
        String token = java.util.UUID.randomUUID().toString();

        // Save token to database
        PasswordResetToken resetToken = new PasswordResetToken(token, email);
        tokenRepository.save(resetToken);

        // Send email
        try {
            emailService.sendPasswordResetEmail(email, token);
        } catch (MessagingException e) {
            throw new RuntimeException(
                    "Failed to send reset email. Please try again."
            );
        }
    }

    public void resetPassword(String token, String newPassword) {
        // Find the token
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException(
                        "Invalid or expired reset link"
                ));

        // Check if expired
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException(
                    "Reset link has expired. Please request a new one."
            );
        }

        // Check if already used
        if (resetToken.isUsed()) {
            throw new RuntimeException(
                    "Reset link has already been used."
            );
        }

        // Find user and update password
        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}