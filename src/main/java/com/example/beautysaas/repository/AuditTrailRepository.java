package com.example.beautysaas.repository;

import com.example.beautysaas.entity.AuditTrail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {

    Page<AuditTrail> findByEmailOrderByCreatedAtDesc(String email, Pageable pageable);

    Page<AuditTrail> findByEventTypeOrderByCreatedAtDesc(
            AuditTrail.AuditEventType eventType, Pageable pageable);

    List<AuditTrail> findByEmailAndEventTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
            String email,
            AuditTrail.AuditEventType eventType,
            LocalDateTime startTime,
            LocalDateTime endTime);

    @Query("SELECT a FROM AuditTrail a WHERE a.severity IN ('ERROR', 'CRITICAL') " +
           "AND a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<AuditTrail> findRecentSecurityIncidents(@Param("since") LocalDateTime since);

    @Query("SELECT a FROM AuditTrail a WHERE a.email = :email AND a.eventType = :eventType " +
           "AND a.status = :status AND a.createdAt >= :since")
    List<AuditTrail> findRecentEventsByTypeAndStatus(
            @Param("email") String email,
            @Param("eventType") AuditTrail.AuditEventType eventType,
            @Param("status") AuditTrail.EventStatus status,
            @Param("since") LocalDateTime since);

    @Query("SELECT DISTINCT a.email FROM AuditTrail a WHERE a.eventType = :eventType " +
           "AND a.status = :status AND a.createdAt >= :since")
    List<String> findUsersWithRecentEventType(
            @Param("eventType") AuditTrail.AuditEventType eventType,
            @Param("status") AuditTrail.EventStatus status,
            @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(a) > 0 FROM AuditTrail a WHERE a.email = :email " +
           "AND a.eventType = :eventType AND a.status = :status AND a.createdAt >= :since")
    boolean hasRecentEvent(
            @Param("email") String email,
            @Param("eventType") AuditTrail.AuditEventType eventType,
            @Param("status") AuditTrail.EventStatus status,
            @Param("since") LocalDateTime since);
            
    long countByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT a FROM AuditTrail a WHERE a.createdAt BETWEEN :startTime AND :endTime AND a.severity IN ('HIGH', 'CRITICAL')")
    List<AuditTrail> findSecurityIncidents(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT a FROM AuditTrail a WHERE a.createdAt BETWEEN :startTime AND :endTime")
    List<AuditTrail> findUserActivity(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT a FROM AuditTrail a WHERE a.createdAt BETWEEN :startTime AND :endTime AND a.resourceType IS NOT NULL")
    List<AuditTrail> findResourceAccess(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query(value = "SELECT a.* FROM audit_trails a WHERE " +
           "to_tsvector('english', a.event_details || ' ' || a.metadata) @@ to_tsquery('english', :searchQuery) " +
           "ORDER BY a.created_at DESC", nativeQuery = true)
    Page<AuditTrail> searchAuditTrails(@Param("searchQuery") String searchQuery, Pageable pageable);
}
