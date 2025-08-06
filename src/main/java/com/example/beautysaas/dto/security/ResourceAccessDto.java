package com.example.beautysaas.dto.security;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResourceAccessDto {
    private String resourceType;
    private String resourceId;
    private long accessCount;
    private long uniqueUsers;
    private String mostFrequentAction;
    private LocalDateTime lastAccessed;
}
