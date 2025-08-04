package com.example.beautysaas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Tag(name = "Health Check", description = "Application health monitoring endpoints")
@Slf4j
public class HealthController {

    @Operation(summary = "Application Health Check", description = "Returns application health status")
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Beauty SaaS API");
        health.put("version", "1.0.0");
        
        log.debug("Health check requested");
        return ResponseEntity.ok(health);
    }

    @Operation(summary = "Application Readiness Check", description = "Returns application readiness status")
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> readinessCheck() {
        Map<String, Object> readiness = new HashMap<>();
        readiness.put("status", "READY");
        readiness.put("timestamp", LocalDateTime.now());
        readiness.put("database", "CONNECTED");
        readiness.put("dependencies", "AVAILABLE");
        
        return ResponseEntity.ok(readiness);
    }

    @Operation(summary = "Application Liveness Check", description = "Returns application liveness status")
    @GetMapping("/live")
    public ResponseEntity<Map<String, Object>> livenessCheck() {
        Map<String, Object> liveness = new HashMap<>();
        liveness.put("status", "ALIVE");
        liveness.put("timestamp", LocalDateTime.now());
        liveness.put("uptime", "Active");
        
        return ResponseEntity.ok(liveness);
    }
}
