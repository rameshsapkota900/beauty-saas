package com.example.beautysaas.dto.security;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SecurityIncidentDto {
    private LocalDateTime timestamp;
    private String eventType;
    private String severity;
    private String userId;
    private String ipAddress;
    private String userAgent;
    private String resourceType;
    private String resourceId;
    private String details;
    private double riskScore;
}
