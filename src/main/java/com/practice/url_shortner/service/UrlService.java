package com.practice.url_shortner.service;

import com.practice.url_shortner.dto.UrlShortenRequest;
import com.practice.url_shortner.dto.UrlShortenResponse;
import com.practice.url_shortner.exception.UrlNotFoundException;
import com.practice.url_shortner.model.Url;
import com.practice.url_shortner.repository.UrlRepository;
import com.practice.url_shortner.util.Base62Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.practice.url_shortner.exception.UrlNotFoundException;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;
import java.util.List;
import com.practice.url_shortner.model.Click;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.practice.url_shortner.dto.UrlAnalyticsResponse;
import com.practice.url_shortner.exception.DuplicateAliasException;
import com.practice.url_shortner.exception.InvalidUrlException;
import com.practice.url_shortner.exception.UrlExpiredException;
import com.practice.url_shortner.model.User;
import com.practice.url_shortner.repository.UserRepository;
import com.practice.url_shortner.dto.UrlSummaryResponse;
import java.util.stream.Collectors;
import com.practice.url_shortner.repository.ClickRepository;
import com.practice.url_shortner.model.Click;

@Service
public class UrlService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final long CACHE_EXPIRY_SECONDS = 3600; // 1 hour

    @Autowired
    private ClickRepository clickRepository;
    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private Base62Encoder base62Encoder;
    @Autowired
    private RateLimitService rateLimitService;


    private static final String BASE_URL = "http://localhost:8080/";


    public String getOriginalUrl(String shortCode,
                                 String ipAddress,
                                 String userAgent) {
        String cacheKey = "url:" + shortCode;

        String cachedUrl = redisTemplate.opsForValue().get(cacheKey);
        if (cachedUrl != null) {
            System.out.println("CACHE HIT for: " + shortCode);
            saveClick(shortCode, ipAddress, userAgent);
            return cachedUrl;
        }

        System.out.println("CACHE MISS for: " + shortCode);

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        if (!url.getIsActive()) throw new UrlNotFoundException(shortCode);

        if (url.getExpiryDate() != null &&
                url.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException(shortCode);
        }

        redisTemplate.opsForValue().set(
                cacheKey, url.getLongUrl(),
                CACHE_EXPIRY_SECONDS, TimeUnit.SECONDS
        );

        saveClick(shortCode, ipAddress, userAgent);

        return url.getLongUrl();
    }

    private void saveClick(String shortCode,
                           String ipAddress,
                           String userAgent) {
        Click click = new Click(shortCode, ipAddress, userAgent);
        clickRepository.save(click);
        incrementClickCount(shortCode);
    }
    private void incrementClickCount(String shortCode) {
        urlRepository.findByShortCode(shortCode).ifPresent(url -> {
            url.setClickCount(url.getClickCount() + 1);
            urlRepository.save(url);
            System.out.println("Click count updated for: " + shortCode
                    + " → " + url.getClickCount());
        });
    }
    public UrlAnalyticsResponse getAnalytics(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        String status;
        if (!url.getIsActive()) status = "INACTIVE";
        else if (url.getExpiryDate() != null &&
                url.getExpiryDate().isBefore(LocalDateTime.now()))
            status = "EXPIRED";
        else status = "ACTIVE";

        // Get recent clicks with details
        List<Click> clicks = clickRepository
                .findByShortCodeOrderByClickedAtDesc(shortCode);

        List<UrlAnalyticsResponse.ClickDetail> clickDetails = clicks.stream()
                .limit(10)
                .map(c -> new UrlAnalyticsResponse.ClickDetail(
                        c.getClickedAt().toString(),
                        maskIp(c.getIpAddress()),
                        parseDevice(c.getUserAgent())
                ))
                .collect(Collectors.toList());

        UrlAnalyticsResponse response = new UrlAnalyticsResponse(
                shortCode,
                url.getLongUrl(),
                BASE_URL + shortCode,
                url.getClickCount(),
                url.getCreatedAt().toString(),
                url.getExpiryDate() != null ?
                        url.getExpiryDate().toString() : "No expiry",
                url.getIsActive(),
                status
        );
        response.setRecentClicks(clickDetails);
        // Get clicks by date for last 7 days
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Object[]> rawChartData = clickRepository
                .findClicksByDateSince(shortCode, sevenDaysAgo);

        List<UrlAnalyticsResponse.ChartData> chartData = rawChartData.stream()
                .map(row -> new UrlAnalyticsResponse.ChartData(
                        row[0].toString(),
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());

        response.setClicksByDate(chartData);
        return response;
    }

    private String maskIp(String ip) {
        if (ip == null) return "Unknown";
        int lastDot = ip.lastIndexOf('.');
        if (lastDot == -1) return ip;
        return ip.substring(0, lastDot) + ".xxx";
    }

    private String parseDevice(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Mobile")) return "📱 Mobile";
        if (userAgent.contains("Tablet")) return "📱 Tablet";
        return "🖥️ Desktop";
    }
    @Autowired
    private UserRepository userRepository;

    // UPDATE the shortenUrl method signature to accept email
    public UrlShortenResponse shortenUrl(UrlShortenRequest request, String userEmail) {
        rateLimitService.checkRateLimit(userEmail);
        // Validation 1 — longUrl must not be null or empty
        if (request.getLongUrl() == null ||
                request.getLongUrl().trim().isEmpty()) {
            throw new InvalidUrlException("URL cannot be empty");
        }

        // Validation 2 — must start with http:// or https://
        if (!request.getLongUrl().startsWith("http://") &&
                !request.getLongUrl().startsWith("https://")) {
            throw new InvalidUrlException(
                    "Invalid URL format — must start with http:// or https://"
            );
        }

        // Validation 3 — custom alias checks
        if (request.getCustomAlias() != null &&
                !request.getCustomAlias().isEmpty()) {
            if (request.getCustomAlias().length() < 3 ||
                    request.getCustomAlias().length() > 50) {
                throw new InvalidUrlException(
                        "Custom alias must be between 3 and 50 characters"
                );
            }
            if (!request.getCustomAlias().matches("^[a-zA-Z0-9-]+$")) {
                throw new InvalidUrlException(
                        "Custom alias can only contain letters, numbers, and hyphens"
                );
            }
            if (urlRepository.existsByCustomAlias(request.getCustomAlias())) {
                throw new DuplicateAliasException(request.getCustomAlias());
            }
        }

        // Find the logged-in user
        User user = userRepository.findByEmail(userEmail)
                .orElse(null);
        // ↑ orElse(null) because old/anonymous requests won't have a user

        // Save URL with temporary short code
        Url url = new Url();
        url.setLongUrl(request.getLongUrl().trim());
        url.setShortCode("tmp_" + java.util.UUID.randomUUID().toString().substring(0, 8)); // ← REPLACE
        url.setCreatedAt(LocalDateTime.now());
        url.setClickCount(0L);
        url.setIsActive(true);
        url.setUser(user); // ← LINK TO USER

        if (request.getCustomAlias() != null &&
                !request.getCustomAlias().isEmpty()) {
            url.setCustomAlias(request.getCustomAlias());
        }

        if (request.getExpiryDate() != null &&
                !request.getExpiryDate().isEmpty()) {
            LocalDateTime expiry = LocalDateTime.parse(
                    request.getExpiryDate() + "T23:59:59",
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
            );
            url.setExpiryDate(expiry);
        }

        Url savedUrl = urlRepository.save(url);

        String shortCode = base62Encoder.encode(savedUrl.getId());
        if (request.getCustomAlias() != null &&
                !request.getCustomAlias().isEmpty()) {
            shortCode = request.getCustomAlias();
        }

        savedUrl.setShortCode(shortCode);
        urlRepository.save(savedUrl);

        String shortUrl = BASE_URL + shortCode;
        String expiryDate = savedUrl.getExpiryDate() != null ?
                savedUrl.getExpiryDate().toString() : "No expiry";

        return new UrlShortenResponse(
                shortUrl,
                request.getLongUrl(),
                shortCode,
                expiryDate
        );
    }
    public String reactivateUrl(String shortCode, String userEmail) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        if (url.getUser() == null ||
                !url.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException(
                    "You don't have permission to reactivate this URL"
            );
        }

        url.setIsActive(true);
        urlRepository.save(url);

        // Re-cache in Redis
        redisTemplate.opsForValue().set(
                "url:" + shortCode,
                url.getLongUrl(),
                3600,
                java.util.concurrent.TimeUnit.SECONDS
        );

        return "URL reactivated: " + shortCode;
    }
    // ADD this new method — get all URLs for a user
    public List<Url> getUserUrls(String userEmail) {
        return urlRepository.findByUserEmail(userEmail);
    }

    // ADD this new method — deactivate a URL
    public String deactivateUrl(String shortCode, String userEmail) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        // Check if this URL belongs to the requesting user
        if (url.getUser() == null ||
                !url.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException(
                    "You don't have permission to deactivate this URL"
            );
        }

        url.setIsActive(false);
        urlRepository.save(url);

        // Remove from Redis cache too
        redisTemplate.delete("url:" + shortCode);

        return "URL deactivated: " + shortCode;
    }
    // Helper method — converts Url entity to UrlSummaryResponse
    private UrlSummaryResponse toSummaryResponse(Url url) {
        String status;
        if (!url.getIsActive()) {
            status = "INACTIVE";
        } else if (url.getExpiryDate() != null &&
                url.getExpiryDate().isBefore(LocalDateTime.now())) {
            status = "EXPIRED";
        } else {
            status = "ACTIVE";
        }

        return new UrlSummaryResponse(
                url.getShortCode(),
                BASE_URL + url.getShortCode(),
                url.getLongUrl(),
                url.getClickCount(),
                url.getCreatedAt().toString(),
                url.getExpiryDate() != null ?
                        url.getExpiryDate().toString() : "No expiry",
                status,
                url.getIsActive()
        );
    }

    // Get all URLs for a user as summary responses
    public List<UrlSummaryResponse> getUserUrlSummaries(String userEmail) {
        return urlRepository
                .findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    // Get only active URLs for a user
    public List<UrlSummaryResponse> getUserActiveUrls(String userEmail) {
        return urlRepository
                .findByUserEmailAndIsActiveOrderByCreatedAtDesc(userEmail, true)
                .stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    // Get top clicked URLs for a user
    public List<UrlSummaryResponse> getUserTopUrls(String userEmail) {
        return urlRepository
                .findByUserEmailOrderByClickCountDesc(userEmail)
                .stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    // Get dashboard summary stats for a user
    public java.util.Map<String, Object> getUserStats(String userEmail) {
        long totalUrls = urlRepository.countByUserEmail(userEmail);
        long activeUrls = urlRepository.countByUserEmailAndIsActive(userEmail, true);
        long inactiveUrls = totalUrls - activeUrls;

        // Total clicks across all URLs
        long totalClicks = urlRepository
                .findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .mapToLong(Url::getClickCount)
                .sum();

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalUrls", totalUrls);
        stats.put("activeUrls", activeUrls);
        stats.put("inactiveUrls", inactiveUrls);
        stats.put("totalClicks", totalClicks);
        return stats;
    }
}
