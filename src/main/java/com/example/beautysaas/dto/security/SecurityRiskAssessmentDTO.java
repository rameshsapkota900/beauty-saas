package com.example.beautysaas.dto.security;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class SecurityRiskAssessmentDTO {
    private String email;
    private Double currentRiskScore;
    private String securityLevel;
    private List<String> knownDevices;
    private List<String> knownLocations;
    private Integer recentFailedAttempts;
    private LocalDateTime lastFailedAttempt;
    private Map<String, Double> riskFactors;
    private List<SecurityIncidentDTO> recentIncidents;
}
