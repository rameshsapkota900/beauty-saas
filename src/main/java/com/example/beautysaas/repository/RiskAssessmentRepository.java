package com.example.beautysaas.repository;

import com.example.beautysaas.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {

    List<RiskAssessment> findByEmailOrderByCreatedAtDesc(String email);

    List<RiskAssessment> findByEmailAndRiskLevelAndIsResolvedFalseOrderByCreatedAtDesc(
            String email, RiskAssessment.RiskLevel riskLevel);

    @Query("SELECT r FROM RiskAssessment r WHERE r.email = :email AND r.createdAt >= :since " +
           "ORDER BY r.createdAt DESC")
    List<RiskAssessment> findRecentAssessments(
            @Param("email") String email,
            @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(r) FROM RiskAssessment r WHERE r.email = :email " +
           "AND r.riskLevel IN ('HIGH', 'CRITICAL') AND r.createdAt >= :since")
    long countHighRiskAssessments(
            @Param("email") String email,
            @Param("since") LocalDateTime since);

    @Query("SELECT AVG(r.riskScore) FROM RiskAssessment r WHERE r.email = :email " +
           "AND r.createdAt >= :since")
    Double calculateAverageRiskScore(
            @Param("email") String email,
            @Param("since") LocalDateTime since);

    @Query("SELECT r FROM RiskAssessment r WHERE r.isResolved = false " +
           "AND r.riskLevel IN ('HIGH', 'CRITICAL') ORDER BY r.createdAt DESC")
    List<RiskAssessment> findUnresolvedHighRiskAssessments();

    @Query("SELECT DISTINCT r.email FROM RiskAssessment r WHERE r.isResolved = false " +
           "AND r.riskLevel = 'CRITICAL' AND r.createdAt >= :since")
    List<String> findUsersWithCriticalRisk(@Param("since") LocalDateTime since);
}
