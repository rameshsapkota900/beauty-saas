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

    public enum ChallengeType {
        SECURITY_QUESTION,
        CAPTCHA,
        EMAIL_VERIFICATION,
        PHONE_VERIFICATION,
        ADMIN_APPROVAL,
        RISK_BASED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (expiresAt == null) {
            expiresAt = createdAt.plusMinutes(30); // Default 30-minute expiration
        }
    }

    public void incrementAttempts() {
        this.attemptCount++;
    }

    public void complete() {
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
