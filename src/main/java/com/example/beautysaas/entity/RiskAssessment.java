package com.example.beautysaas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_assessments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "risk_score")
    private Double riskScore;

    @Column(name = "risk_level")
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(name = "assessment_type")
    @Enumerated(EnumType.STRING)
    private AssessmentType assessmentType;

    @Column(name = "risk_factors")
    private String riskFactors;

    @Column(name = "recommended_actions")
    private String recommendedActions;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "action_taken")
    private String actionTaken;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "is_resolved")
    @Builder.Default
    private Boolean isResolved = false;

    public enum RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    public enum AssessmentType {
        LOGIN_ATTEMPT,
        PASSWORD_CHANGE,
        PROFILE_UPDATE,
        PAYMENT_TRANSACTION,
        API_ACCESS,
        ADMIN_ACTION,
        SUSPICIOUS_ACTIVITY
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void resolve(String action) {
        this.isResolved = true;
        this.actionTaken = action;
        this.resolvedAt = LocalDateTime.now();
    }

    public static RiskLevel calculateRiskLevel(double riskScore) {
        if (riskScore >= 80) {
            return RiskLevel.CRITICAL;
        } else if (riskScore >= 60) {
            return RiskLevel.HIGH;
        } else if (riskScore >= 40) {
            return RiskLevel.MEDIUM;
        } else {
            return RiskLevel.LOW;
        }
    }
}
