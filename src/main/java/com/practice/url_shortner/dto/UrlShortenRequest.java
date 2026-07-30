package com.practice.url_shortner.dto;

public class UrlShortenRequest {

    private String longUrl;
    private String customAlias;   // optional
    private String expiryDate;    // optional, format: "2024-12-31"

    public UrlShortenRequest() {
    }

    public String getLongUrl() { return longUrl; }
    public void setLongUrl(String longUrl) { this.longUrl = longUrl; }

    public String getCustomAlias() { return customAlias; }
    public void setCustomAlias(String customAlias) { this.customAlias = customAlias; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}