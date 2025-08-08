package com.example.beautysaas.dto.security;

import lombok.Data;

@Data
public class SecurityLevelDTO {
    private String email;
    private String currentLevel;
    private String recommendedLevel;
    private String reason;
    private Integer maxAttempts;
    private Integer expirationMinutes;
    private Boolean requiresStepUp;
}
