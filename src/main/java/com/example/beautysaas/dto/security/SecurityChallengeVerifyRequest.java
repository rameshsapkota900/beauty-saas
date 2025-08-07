package com.example.beautysaas.dto.security;

import lombok.Data;

@Data
public class SecurityChallengeVerifyRequest {
    private Long challengeId;
    private String verificationToken;
    private String answer;
    private String ipAddress;
    private String userAgent;
}
