package com.practice.url_shortner.repository;

import com.practice.url_shortner.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    List<Url> findByUserEmail(String email);
// ↑ finds all URLs belonging to a specific user's email
//   Spring generates: SELECT * FROM urls WHERE user_id =
//                     (SELECT id FROM users WHERE email = ?)

    List<Url> findByUserEmailAndIsActive(String email, Boolean isActive);
    // ↑ finds only active/inactive URLs for a user
    // Find URL by short code — used for redirect
    Optional<Url> findByShortCode(String shortCode);
    // Get all URLs for a user — sorted by newest first
    List<Url> findByUserEmailOrderByCreatedAtDesc(String email);

    // Get only active URLs for a user
    List<Url> findByUserEmailAndIsActiveOrderByCreatedAtDesc(
            String email, Boolean isActive);

    // Get top clicked URLs for a user
    List<Url> findByUserEmailOrderByClickCountDesc(String email);

    // Count total URLs for a user
    long countByUserEmail(String email);

    // Count active URLs for a user
    long countByUserEmailAndIsActive(String email, Boolean isActive);

    // Find URL by custom alias — used when user provides own short name
    Optional<Url> findByCustomAlias(String customAlias);

    // Check if short code already exists — used when generating new codes
    boolean existsByShortCode(String shortCode);

    // Check if custom alias already exists — used when user picks their own
    boolean existsByCustomAlias(String customAlias);
}