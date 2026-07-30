package com.practice.url_shortner.repository;

import com.practice.url_shortner.model.Click;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;


import java.util.List;

@Repository
public interface ClickRepository extends JpaRepository<Click, Long> {

    List<Click> findByShortCodeOrderByClickedAtDesc(String shortCode);

    long countByShortCode(String shortCode);
@Query("SELECT DATE(c.clickedAt) as date, COUNT(c) as count " +
        "FROM Click c WHERE c.shortCode = :shortCode " +
        "AND c.clickedAt >= :since " +
        "GROUP BY DATE(c.clickedAt) ORDER BY DATE(c.clickedAt)")
List<Object[]> findClicksByDateSince(
        @Param("shortCode") String shortCode,
        @Param("since") LocalDateTime since
);
}

