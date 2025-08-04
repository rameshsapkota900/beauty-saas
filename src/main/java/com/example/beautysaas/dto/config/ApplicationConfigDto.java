package com.example.beautysaas.dto.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationConfigDto {
    private SecurityConfig security;
    private PasswordPolicy passwordPolicy;
    private SystemSettings systemSettings;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecurityConfig {
        private int maxLoginAttempts;
        private int lockoutDurationMinutes;
        private int maxConcurrentSessions;
        private boolean preventSessionFixation;
        private long jwtExpirationMilliseconds;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordPolicy {
        private int minLength;
        private boolean requireUppercase;
        private boolean requireLowercase;
        private boolean requireDigits;
        private boolean requireSpecialChars;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemSettings {
        private String applicationName;
        private String version;
        private boolean maintenanceMode;
        private String supportEmail;
    }
}
