package com.example.beautysaas.dto.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitExceededResponse {
    private String error;
    private String retryAfter;
    private long remainingTimeInSeconds;
}
