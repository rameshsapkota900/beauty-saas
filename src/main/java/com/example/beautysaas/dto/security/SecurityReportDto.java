package com.example.beautysaas.dto.security;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class SecurityReportDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long totalEvents;
    private long suspiciousEvents;
    private long highRiskEvents;
    private long uniqueUsers;
    private Map<String, Long> eventsByType;
    private Map<String, Long> eventsBySeverity;
    private List<SecurityIncidentDto> recentIncidents;
    private List<ResourceAccessDto> hotspots;
}
