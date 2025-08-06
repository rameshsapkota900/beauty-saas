package com.example.beautysaas.dto.security;

import lombok.Data;
import java.util.Map;

@Data
public class SecurityNotificationDto {
    private String recipient;
    private String subject;
    private Map<String, Object> details;
    private boolean urgent;
    private String notificationType;
}
