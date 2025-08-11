package com.example.beautysaas.dto.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityIncidentDTO {
    private Long id;
    private String userId;
    private String eventType;
    private String severity;
    private String details;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;
    private String resourceType;
    private String resourceId;
    private double riskScore;
}
