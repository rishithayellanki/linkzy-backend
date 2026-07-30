package com.practice.url_shortner.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "urls")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "long_url", nullable = false, length = 2048)
    private String longUrl;

    @Column(name = "short_code", nullable = false, unique = true, length = 20)
    private String shortCode;

    @Column(name = "custom_alias", unique = true, length = 50)
    private String customAlias;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "click_count", nullable = false)
    private Long clickCount = 0L;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // NEW — link to the user who created this URL
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    // ↑ nullable = true because old URLs don't have a user yet
    @JsonIgnore
    private User user;

    // Empty constructor
    public Url() {
    }

    public Url(String longUrl, String shortCode, LocalDateTime createdAt) {
        this.longUrl = longUrl;
        this.shortCode = shortCode;
        this.createdAt = createdAt;
        this.clickCount = 0L;
        this.isActive = true;
    }

    // All existing getters/setters stay the same
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLongUrl() { return longUrl; }
    public void setLongUrl(String longUrl) { this.longUrl = longUrl; }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public String getCustomAlias() { return customAlias; }
    public void setCustomAlias(String customAlias) { this.customAlias = customAlias; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public Long getClickCount() { return clickCount; }
    public void setClickCount(Long clickCount) { this.clickCount = clickCount; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    // NEW getter/setter for user
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

}