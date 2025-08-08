package com.example.beautysaas.dto.security;

import lombok.Data;

@Data
public class SecurityLevelUpdateRequest {
    private String securityLevel;
    private String reason;
    private Boolean temporary;
    private Integer durationMinutes;
}
