package com.example.beautysaas.dto.security;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SecurityChallengeResponse {
    private boolean success;
    private String message;
    private Long challengeId;
    private LocalDateTime completedAt;
    private Integer attemptCount;
    private Boolean isCompleted;
}
