package com.practice.url_shortner.dto;

public class UrlSummaryResponse {

    private String shortCode;
    private String shortUrl;
    private String longUrl;
    private Long clickCount;
    private String createdAt;
    private String expiryDate;
    private String status;
    private Boolean isActive;

    public UrlSummaryResponse() {
    }

    public UrlSummaryResponse(String shortCode, String shortUrl,
                              String longUrl, Long clickCount,
                              String createdAt, String expiryDate,
                              String status, Boolean isActive) {
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.clickCount = clickCount;
        this.createdAt = createdAt;
        this.expiryDate = expiryDate;
        this.status = status;
        this.isActive = isActive;
    }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public String getShortUrl() { return shortUrl; }
    public void setShortUrl(String shortUrl) { this.shortUrl = shortUrl; }

    public String getLongUrl() { return longUrl; }
    public void setLongUrl(String longUrl) { this.longUrl = longUrl; }

    public Long getClickCount() { return clickCount; }
    public void setClickCount(Long clickCount) { this.clickCount = clickCount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}