package com.example.beautysaas.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "audit.retention")
@Data
public class AuditRetentionConfig {
    private boolean enabled = true;
    private String schedule = "0 0 1 * * ?";
    private int retentionMonths = 6;
    private int criticalRetentionMonths = 24;
    private int batchSize = 1000;
}
