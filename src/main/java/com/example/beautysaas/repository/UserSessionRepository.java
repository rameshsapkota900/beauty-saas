package com.example.beautysaas.repository;

import com.example.beautysaas.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {
    
    List<UserSession> findByEmailAndIsActiveTrue(String email);
    
    Optional<UserSession> findBySessionIdAndIsActiveTrue(String sessionId);
    
    @Query("SELECT s FROM UserSession s WHERE s.email = :email AND s.isActive = true " +
           "AND s.lastActivity > :threshold")
    List<UserSession> findActiveSessions(@Param("email") String email, 
                                       @Param("threshold") LocalDateTime threshold);
    
    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false, s.revocationReason = :reason " +
           "WHERE s.email = :email AND s.isActive = true AND s.sessionId != :excludeSessionId")
    void deactivateOtherSessions(@Param("email") String email, 
                                @Param("excludeSessionId") String excludeSessionId,
                                @Param("reason") String reason);
    
    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false, s.revocationReason = 'Session expired' " +
           "WHERE s.isActive = true AND s.lastActivity < :threshold")
    void deactivateExpiredSessions(@Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT COUNT(s) FROM UserSession s WHERE s.email = :email AND s.isActive = true " +
           "AND s.lastActivity > :threshold")
    long countActiveSessions(@Param("email") String email, @Param("threshold") LocalDateTime threshold);
}
