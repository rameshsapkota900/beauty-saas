package com.example.beautysaas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_challenges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(name = "challenge_type")
    @Enumerated(EnumType.STRING)
    private ChallengeType challengeType;

    @Column(name = "challenge_data")
    private String challengeData;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "attempt_count")
    @Builder.Default
    private Integer attemptCount = 0;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "security_level")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SecurityLevel securityLevel = SecurityLevel.MEDIUM;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "last_attempt_time")
    private LocalDateTime lastAttemptTime;

    @Column(name = "device_fingerprint")
    private String deviceFingerprint;

    @Column(name = "geolocation")
    private String geolocation;

    @Column(name = "risk_score")
    @Builder.Default
    private Double riskScore = 0.0;

    public enum ChallengeType {
        SECURITY_QUESTION,
        CAPTCHA,
        EMAIL_VERIFICATION,
        PHONE_VERIFICATION,
        ADMIN_APPROVAL,
        RISK_BASED,
        DEVICE_VERIFICATION,
        LOCATION_VERIFICATION,
        BEHAVIORAL_VERIFICATION
    }

    public enum SecurityLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (expiresAt == null) {
            expiresAt = createdAt.plusMinutes(getExpirationMinutes());
        }
    }

    public void incrementAttempts() {
        this.attemptCount++;
        this.lastAttemptTime = LocalDateTime.now();
        
        if (this.attemptCount >= getMaxAttempts()) {
            this.failureReason = "Maximum attempts exceeded";
            this.expiresAt = LocalDateTime.now();
        }
    }

    public void complete() {
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
        this.riskScore = 0.0; // Reset risk score on successful completion
    }

    public void fail(String reason) {
        this.failureReason = reason;
        this.riskScore += 0.2; // Increase risk score on failure
        
        if (this.riskScore >= 0.8) {
            this.securityLevel = SecurityLevel.HIGH;
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isTemporarilyBlocked() {
        if (lastAttemptTime == null || attemptCount < 3) {
            return false;
        }
        
        // Implement exponential backoff: 5min, 15min, 30min, 1h, 2h, 4h
        int blockMinutes = (int) Math.min(240, 5 * Math.pow(2, attemptCount - 3));
        return LocalDateTime.now().isBefore(lastAttemptTime.plusMinutes(blockMinutes));
    }

    public int getMaxAttempts() {
        return switch (securityLevel) {
            case LOW -> 5;
            case MEDIUM -> 3;
            case HIGH -> 2;
            case CRITICAL -> 1;
        };
    }

    private int getExpirationMinutes() {
        return switch (securityLevel) {
            case LOW -> 60;
            case MEDIUM -> 30;
            case HIGH -> 15;
            case CRITICAL -> 5;
        };
    }

    public void updateRiskScore(double score) {
        this.riskScore = score;
        
        // Adjust security level based on risk score
        if (score >= 0.8) {
            this.securityLevel = SecurityLevel.CRITICAL;
        } else if (score >= 0.6) {
            this.securityLevel = SecurityLevel.HIGH;
        } else if (score >= 0.4) {
            this.securityLevel = SecurityLevel.MEDIUM;
        } else {
            this.securityLevel = SecurityLevel.LOW;
        }
    }

    public void recordDeviceInfo(String fingerprint, String location) {
        this.deviceFingerprint = fingerprint;
        this.geolocation = location;
    }
}
