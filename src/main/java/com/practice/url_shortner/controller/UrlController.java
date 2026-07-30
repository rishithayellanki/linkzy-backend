package com.practice.url_shortner.controller;

import com.practice.url_shortner.dto.UrlShortenRequest;
import com.practice.url_shortner.dto.UrlShortenResponse;
import com.practice.url_shortner.dto.UrlSummaryResponse;
import com.practice.url_shortner.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.practice.url_shortner.dto.UrlAnalyticsResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class UrlController {

    @Autowired
    private UrlService urlService;

    // Helper — gets logged-in user's email from JWT token
    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        return auth.getName();
    }

    // POST /api/shorten — create short URL (protected)
    @PostMapping("/api/shorten")
    public UrlShortenResponse shortenUrl(@RequestBody UrlShortenRequest request) {
        String userEmail = getCurrentUserEmail();
        return urlService.shortenUrl(request, userEmail);
    }

    // GET /api/my-urls — all URLs with summary (protected)
    @GetMapping("/api/my-urls")
    public List<UrlSummaryResponse> getMyUrls() {
        String userEmail = getCurrentUserEmail();
        return urlService.getUserUrlSummaries(userEmail);
    }

    // GET /api/my-urls/active — only active URLs (protected)
    @GetMapping("/api/my-urls/active")
    public List<UrlSummaryResponse> getMyActiveUrls() {
        String userEmail = getCurrentUserEmail();
        return urlService.getUserActiveUrls(userEmail);
    }
    @GetMapping("/api/analytics/{shortCode}")
    public UrlAnalyticsResponse getAnalytics(@PathVariable String shortCode) {
        return urlService.getAnalytics(shortCode);
    }

    // GET /api/my-urls/top — top clicked URLs (protected)
    @GetMapping("/api/my-urls/top")
    public List<UrlSummaryResponse> getMyTopUrls() {
        String userEmail = getCurrentUserEmail();
        return urlService.getUserTopUrls(userEmail);
    }

    // GET /api/my-stats — dashboard summary stats (protected)
    @GetMapping("/api/my-stats")
    public Map<String, Object> getMyStats() {
        String userEmail = getCurrentUserEmail();
        return urlService.getUserStats(userEmail);
    }

    // DELETE /api/my-urls/{shortCode} — deactivate a URL (protected)
    @DeleteMapping("/api/my-urls/{shortCode}")
    public String deactivateUrl(@PathVariable String shortCode) {
        String userEmail = getCurrentUserEmail();
        return urlService.deactivateUrl(shortCode, userEmail);
    }

    // GET /{shortCode} — redirect (public)
    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode,
                         jakarta.servlet.http.HttpServletRequest httpRequest,
                         HttpServletResponse response) throws IOException {

        // Get IP address
        String ipAddress = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }

        // Get browser/device info
        String userAgent = httpRequest.getHeader("User-Agent");

        String longUrl = urlService.getOriginalUrl(shortCode, ipAddress, userAgent);
        response.sendRedirect(longUrl);
    }
    @PutMapping("/api/my-urls/{shortCode}/reactivate")
    public String reactivateUrl(@PathVariable String shortCode) {
        String userEmail = getCurrentUserEmail();
        return urlService.reactivateUrl(shortCode, userEmail);
    }
}