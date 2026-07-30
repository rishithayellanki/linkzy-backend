package com.practice.url_shortner.dto;
import java.util.List;


public class UrlAnalyticsResponse {

    private String shortCode;
    private String longUrl;
    private String shortUrl;
    private Long clickCount;
    private String createdAt;
    private String expiryDate;
    private Boolean isActive;
    private String status; // "ACTIVE", "EXPIRED", or "INACTIVE"
    private List<ClickDetail> recentClicks;
    private List<ChartData> clicksByDate;

    public UrlAnalyticsResponse() {
    }

    public UrlAnalyticsResponse(String shortCode, String longUrl,
                                String shortUrl, Long clickCount,
                                String createdAt, String expiryDate,
                                Boolean isActive, String status) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
        this.clickCount = clickCount;
        this.createdAt = createdAt;
        this.expiryDate = expiryDate;
        this.isActive = isActive;
        this.status = status;
    }

    // Getters and Setters
    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ClickDetail> getRecentClicks() {
        return recentClicks;
    }

    public void setRecentClicks(List<ClickDetail> recentClicks) {
        this.recentClicks = recentClicks;
    }

    public static class ClickDetail {
        public String clickedAt;
        public String ipAddress;
        public String device;

        public ClickDetail(String clickedAt, String ipAddress, String device) {
            this.clickedAt = clickedAt;
            this.ipAddress = ipAddress;
            this.device = device;
        }
    }
    public List<ChartData> getClicksByDate() { return clicksByDate; }
    public void setClicksByDate(List<ChartData> clicksByDate) {
        this.clicksByDate = clicksByDate;
    }

    // Add inner class
    public static class ChartData {
        public String date;
        public Long count;

        public ChartData(String date, Long count) {
            this.date = date;
            this.count = count;
        }
    }
}