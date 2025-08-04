package com.example.beautysaas.service;

import com.example.beautysaas.dto.config.ApplicationConfigDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConfigurationService {
    
    @Value("${security.account-lockout.max-attempts:5}")
    private int maxLoginAttempts;
    
    @Value("${security.account-lockout.lockout-duration-minutes:30}")
    private int lockoutDurationMinutes;
    
    @Value("${security.session.max-concurrent-sessions:3}")
    private int maxConcurrentSessions;
    
    @Value("${security.session.prevent-session-fixation:true}")
    private boolean preventSessionFixation;
    
    @Value("${app.jwt-expiration-milliseconds:604800000}")
    private long jwtExpirationMilliseconds;
    
    @Value("${security.password.min-length:8}")
    private int passwordMinLength;
    
    @Value("${security.password.require-uppercase:true}")
    private boolean requireUppercase;
    
    @Value("${security.password.require-lowercase:true}")
    private boolean requireLowercase;
    
    @Value("${security.password.require-digits:true}")
    private boolean requireDigits;
    
    @Value("${security.password.require-special-chars:true}")
    private boolean requireSpecialChars;
    
    @Value("${spring.application.name:beautysaas}")
    private String applicationName;
    
    @Value("${app.version:1.0.0}")
    private String version;
    
    @Value("${app.maintenance-mode:false}")
    private boolean maintenanceMode;
    
    @Value("${app.support-email:support@beautysaas.com}")
    private String supportEmail;
    
    public ApplicationConfigDto getApplicationConfig() {
        log.debug("Retrieving application configuration");
        
        ApplicationConfigDto.SecurityConfig securityConfig = ApplicationConfigDto.SecurityConfig.builder()
                .maxLoginAttempts(maxLoginAttempts)
                .lockoutDurationMinutes(lockoutDurationMinutes)
                .maxConcurrentSessions(maxConcurrentSessions)
                .preventSessionFixation(preventSessionFixation)
                .jwtExpirationMilliseconds(jwtExpirationMilliseconds)
                .build();
        
        ApplicationConfigDto.PasswordPolicy passwordPolicy = ApplicationConfigDto.PasswordPolicy.builder()
                .minLength(passwordMinLength)
                .requireUppercase(requireUppercase)
                .requireLowercase(requireLowercase)
                .requireDigits(requireDigits)
                .requireSpecialChars(requireSpecialChars)
                .build();
        
        ApplicationConfigDto.SystemSettings systemSettings = ApplicationConfigDto.SystemSettings.builder()
                .applicationName(applicationName)
                .version(version)
                .maintenanceMode(maintenanceMode)
                .supportEmail(supportEmail)
                .build();
        
        return ApplicationConfigDto.builder()
                .security(securityConfig)
                .passwordPolicy(passwordPolicy)
                .systemSettings(systemSettings)
                .build();
    }
    
    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }
    
    public String getSupportEmail() {
        return supportEmail;
    }
}
