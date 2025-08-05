package com.example.beautysaas.repository;

import com.example.beautysaas.entity.SecurityAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, UUID> {
    
    List<SecurityAuditLog> findByEmailOrderByCreatedAtDesc(String email);
    
    @Query("SELECT s FROM SecurityAuditLog s WHERE s.email = :email AND s.createdAt >= :since")
    List<SecurityAuditLog> findByEmailAndCreatedAtAfter(@Param("email") String email, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(s) FROM SecurityAuditLog s WHERE s.eventType = :eventType " +
           "AND s.createdAt BETWEEN :startTime AND :endTime")
    long countByEventTypeAndTimestampBetween(
            @Param("eventType") String eventType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(DISTINCT s.ipAddress) FROM SecurityAuditLog s " +
           "WHERE s.createdAt BETWEEN :startTime AND :endTime AND s.ipAddress IS NOT NULL")
    long countDistinctIpAddressesBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT s FROM SecurityAuditLog s WHERE s.eventType = :eventType AND s.createdAt >= :since")
    List<SecurityAuditLog> findByEventTypeAndCreatedAtAfter(@Param("eventType") String eventType, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(s) FROM SecurityAuditLog s WHERE s.email = :email AND s.eventType = 'LOGIN_FAILURE' AND s.createdAt >= :since")
    Long countFailedLoginAttempts(@Param("email") String email, @Param("since") LocalDateTime since);
}
