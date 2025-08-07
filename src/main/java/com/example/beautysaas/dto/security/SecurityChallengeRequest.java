package com.example.beautysaas.dto.security;

import lombok.Data;

@Data
public class SecurityChallengeRequest {
    private String email;
    private String challengeType;
    private String challengeData;
    private String ipAddress;
    private String userAgent;
}
