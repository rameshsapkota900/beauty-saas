package com.example.beautysaas.dto.security;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SecurityChallengeDTO {
    private Long id;
    private String email;
    private String challengeType;
    private String challengeData;
    private String verificationToken;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private Integer attemptCount;
    private String ipAddress;
    private String userAgent;
    private Boolean isCompleted;
}
