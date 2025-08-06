package com.example.beautysaas.service;

import com.example.beautysaas.entity.AuditTrail;
import com.example.beautysaas.repository.AuditTrailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SecurityReportGeneratorTest {
    @Mock
    private AuditTrailRepository auditTrailRepository;

    @Mock
    private BehaviorAnalyzer behaviorAnalyzer;

    @Mock
    private SessionAnalyzer sessionAnalyzer;

    @InjectMocks
    private SecurityReportGenerator reportGenerator;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        startTime = LocalDateTime.now().minusHours(1);
        endTime = LocalDateTime.now();
    }

    @Test
    void generateSecurityReport_ShouldIncludeBasicStatistics() {
        // Arrange
        when(auditTrailRepository.countByCreatedAtBetween(any(), any())).thenReturn(10L);
        when(auditTrailRepository.findSecurityIncidents(any(), any())).thenReturn(createTestSecurityEvents());
        when(auditTrailRepository.findUserActivity(any(), any())).thenReturn(createTestUserEvents());
        
        // Act
        Map<String, Object> report = reportGenerator.generateSecurityReport(startTime, endTime);
        
        // Assert
        assertNotNull(report);
        assertEquals(10L, report.get("totalEvents"));
        assertNotNull(report.get("securityIncidents"));
        assertNotNull(report.get("userActivitySummary"));
    }

    @Test
    void generateSecurityReport_ShouldAnalyzeSuspiciousActivities() {
        // Arrange
        List<AuditTrail> events = createTestSecurityEvents();
        when(auditTrailRepository.findAll()).thenReturn(events);
        when(behaviorAnalyzer.isAnomalous(anyString(), anyString(), any())).thenReturn(true);
        
        // Act
        Map<String, Object> report = reportGenerator.generateSecurityReport(startTime, endTime);
        
        // Assert
        assertNotNull(report.get("suspiciousActivities"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> suspiciousActivities = (List<Map<String, Object>>) report.get("suspiciousActivities");
        assertFalse(suspiciousActivities.isEmpty());
    }

    @Test
    void generateSecurityReport_ShouldAnalyzeResourceAccess() {
        // Arrange
        when(auditTrailRepository.findResourceAccess(any(), any())).thenReturn(createTestResourceAccessEvents());
        
        // Act
        Map<String, Object> report = reportGenerator.generateSecurityReport(startTime, endTime);
        
        // Assert
        assertNotNull(report.get("resourceAccessPatterns"));
        @SuppressWarnings("unchecked")
        Map<String, Object> resourceAccess = (Map<String, Object>) report.get("resourceAccessPatterns");
        assertNotNull(resourceAccess.get("byResource"));
        assertNotNull(resourceAccess.get("hotspots"));
    }

    private List<AuditTrail> createTestSecurityEvents() {
        AuditTrail event1 = new AuditTrail();
        event1.setEmail("user1@test.com");
        event1.setEventType(AuditTrail.AuditEventType.LOGIN_FAILED);
        event1.setSeverity(AuditTrail.EventSeverity.HIGH);
        event1.setCreatedAt(LocalDateTime.now());

        AuditTrail event2 = new AuditTrail();
        event2.setEmail("user2@test.com");
        event2.setEventType(AuditTrail.AuditEventType.UNAUTHORIZED_ACCESS);
        event2.setSeverity(AuditTrail.EventSeverity.CRITICAL);
        event2.setCreatedAt(LocalDateTime.now());

        return Arrays.asList(event1, event2);
    }

    private List<AuditTrail> createTestUserEvents() {
        AuditTrail event1 = new AuditTrail();
        event1.setEmail("user1@test.com");
        event1.setEventType(AuditTrail.AuditEventType.LOGIN);
        event1.setCreatedAt(LocalDateTime.now());

        AuditTrail event2 = new AuditTrail();
        event2.setEmail("user2@test.com");
        event2.setEventType(AuditTrail.AuditEventType.LOGOUT);
        event2.setCreatedAt(LocalDateTime.now());

        return Arrays.asList(event1, event2);
    }

    private List<AuditTrail> createTestResourceAccessEvents() {
        AuditTrail event1 = new AuditTrail();
        event1.setEmail("user1@test.com");
        event1.setResourceType("document");
        event1.setResourceId("doc123");
        event1.setEventType(AuditTrail.AuditEventType.READ);
        event1.setCreatedAt(LocalDateTime.now());

        AuditTrail event2 = new AuditTrail();
        event2.setEmail("user2@test.com");
        event2.setResourceType("document");
        event2.setResourceId("doc123");
        event2.setEventType(AuditTrail.AuditEventType.MODIFY);
        event2.setCreatedAt(LocalDateTime.now());

        return Arrays.asList(event1, event2);
    }
}
