package com.practice.url_shortner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPasswordResetEmail(String toEmail, String token)
            throws MessagingException {

        String resetLink = frontendUrl + "/reset-password?token=" + token;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("Reset your Linkzy password");

        // HTML email content
        String htmlContent = """
            <div style="font-family: Inter, Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                
                <!-- Header -->
                <div style="background: linear-gradient(135deg, #2563EB, #0EA5E9);
                            padding: 2rem; text-align: center; border-radius: 12px 12px 0 0;">
                    <h1 style="color: white; margin: 0; font-size: 1.75rem; font-weight: 800;">
                        Link<span style="color: #BAE6FD;">zy</span>
                    </h1>
                    <p style="color: rgba(255,255,255,0.8); margin: 0.5rem 0 0;">
                        Shorten. Share. Track.
                    </p>
                </div>
                
                <!-- Body -->
                <div style="background: #F8FAFC; padding: 2rem;
                            border: 1px solid #E2E8F0; border-top: none;">
                    <h2 style="color: #0F172A; font-size: 1.25rem; margin-bottom: 1rem;">
                        Reset your password
                    </h2>
                    <p style="color: #64748B; line-height: 1.6; margin-bottom: 1.5rem;">
                        We received a request to reset the password for your Linkzy account
                        associated with <strong>%s</strong>.
                        Click the button below to reset it.
                    </p>
                    
                    <!-- Reset Button -->
                    <div style="text-align: center; margin: 2rem 0;">
                        <a href="%s"
                           style="background: #2563EB; color: white; padding: 0.875rem 2rem;
                                  border-radius: 8px; text-decoration: none; font-weight: 600;
                                  font-size: 1rem; display: inline-block;">
                            Reset Password →
                        </a>
                    </div>
                    
                    <!-- Warning -->
                    <div style="background: #FEF3C7; border-left: 3px solid #F59E0B;
                                padding: 1rem; border-radius: 4px; margin-bottom: 1.5rem;">
                        <p style="color: #92400E; margin: 0; font-size: 0.875rem;">
                            ⚠️ This link expires in <strong>15 minutes</strong>.
                            If you didn't request this, you can safely ignore this email.
                        </p>
                    </div>
                    
                    <!-- Link fallback -->
                    <p style="color: #94A3B8; font-size: 0.8rem;">
                        If the button doesn't work, copy and paste this link:<br/>
                        <a href="%s" style="color: #2563EB; word-break: break-all;">%s</a>
                    </p>
                </div>
                
                <!-- Footer -->
                <div style="background: #0F172A; padding: 1rem; text-align: center;
                            border-radius: 0 0 12px 12px;">
                    <p style="color: #64748B; font-size: 0.75rem; margin: 0;">
                        © 2024 Linkzy. This is an automated email, please do not reply.
                    </p>
                </div>
            </div>
        """.formatted(toEmail, resetLink, resetLink, resetLink);

        helper.setText(htmlContent, true); // true = HTML
        mailSender.send(message);
    }
}