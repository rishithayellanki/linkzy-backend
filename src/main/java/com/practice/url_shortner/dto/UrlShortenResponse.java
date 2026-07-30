package com.practice.url_shortner.dto;

public class UrlShortenResponse {

    private String shortUrl;
    private String longUrl;
    private String shortCode;
    private String expiryDate;

    public UrlShortenResponse() {
    }

    public UrlShortenResponse(String shortUrl, String longUrl,
                              String shortCode, String expiryDate) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.shortCode = shortCode;
        this.expiryDate = expiryDate;
    }

    public String getShortUrl() { return shortUrl; }
    public void setShortUrl(String shortUrl) { this.shortUrl = shortUrl; }

    public String getLongUrl() { return longUrl; }
    public void setLongUrl(String longUrl) { this.longUrl = longUrl; }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}