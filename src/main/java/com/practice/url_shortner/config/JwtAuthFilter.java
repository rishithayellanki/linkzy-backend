package com.practice.url_shortner.config;

import com.practice.url_shortner.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    // ↑ OncePerRequestFilter ensures this runs exactly
    //   ONCE per request — not multiple times

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Step 1 — Get the Authorization header
        String authHeader = request.getHeader("Authorization");

        // Step 2 — Check if header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No token — pass request along
            // Spring Security will handle unauthorized access
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3 — Extract the token (remove "Bearer " prefix)
        String token = authHeader.substring(7);

        // Step 4 — Validate the token
        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 5 — Extract email from token
        String email = jwtService.extractEmail(token);

        // Step 6 — Set authentication in Security Context
        // This tells Spring Security "this request is authenticated"
        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            new ArrayList<>()  // authorities/roles (empty for now)
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Step 7 — Continue to the next filter/controller
        filterChain.doFilter(request, response);
    }
}