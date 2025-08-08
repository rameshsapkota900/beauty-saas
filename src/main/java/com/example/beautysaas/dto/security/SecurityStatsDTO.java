package com.example.beautysaas.dto.security;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class SecurityStatsDTO {
    private String email;
    private Integer totalChallenges;
    private Integer successfulChallenges;
    private Integer failedChallenges;
    private Double successRate;
    private LocalDateTime lastSuccessfulChallenge;
    private LocalDateTime lastFailedChallenge;
    private Map<String, Integer> challengeTypeBreakdown;
    private Map<String, Integer> failureReasonBreakdown;
    private Integer averageAttemptsPerChallenge;
    private Double averageCompletionTimeSeconds;
}
