package com.example.beautysaas.controller;

import com.example.beautysaas.dto.security.SecurityReportDto;
import com.example.beautysaas.dto.security.SecurityNotificationDto;
import com.example.beautysaas.entity.AuditTrail;
import com.example.beautysaas.repository.AuditTrailRepository;
import com.example.beautysaas.service.SecurityNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuditTrailRepository auditTrailRepository;

    @MockBean
    private SecurityNotificationService notificationService;

    @BeforeEach
    void setUp() {
        auditTrailRepository.deleteAll();
        createTestData();
    }

    @Test
    @WithMockUser(roles = "SECURITY_ADMIN")
    void getSecurityReport_ShouldReturnReport() throws Exception {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/security/report")
                .param("startTime", startTime.toString())
                .param("endTime", endTime.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").exists())
                .andExpect(jsonPath("$.suspiciousEvents").exists());
    }

    @Test
    @WithMockUser(roles = "SECURITY_ADMIN")
    void getCurrentSummary_ShouldReturnSummary() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/security/report/summary")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").exists());
    }

    @Test
    @WithMockUser(roles = "SECURITY_ADMIN")
    void getDailyReport_ShouldReturnDailyReport() throws Exception {
        LocalDateTime date = LocalDateTime.now();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/security/report/daily")
                .param("date", date.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").exists());
    }

    @Test
    @WithMockUser(roles = "SECURITY_ADMIN")
    void sendSecurityNotification_ShouldSendNotification() throws Exception {
        SecurityNotificationDto notification = new SecurityNotificationDto();
        notification.setRecipient("security@test.com");
        notification.setSubject("Test Alert");
        notification.setDetails(new HashMap<>());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/security/notify")
                .content(objectMapper.writeValueAsString(notification))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(notificationService).notifySecurityIncident(
            eq(notification.getRecipient()),
            eq(notification.getSubject()),
            any(Map.class)
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    void getSecurityReport_WithoutProperRole_ShouldReturnForbidden() throws Exception {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/security/report")
                .param("startTime", startTime.toString())
                .param("endTime", endTime.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private void createTestData() {
        AuditTrail event1 = new AuditTrail();
        event1.setEmail("user1@test.com");
        event1.setEventType(AuditTrail.AuditEventType.LOGIN);
        event1.setSeverity(AuditTrail.EventSeverity.INFO);
        event1.setCreatedAt(LocalDateTime.now());
        auditTrailRepository.save(event1);

        AuditTrail event2 = new AuditTrail();
        event2.setEmail("user2@test.com");
        event2.setEventType(AuditTrail.AuditEventType.UNAUTHORIZED_ACCESS);
        event2.setSeverity(AuditTrail.EventSeverity.HIGH);
        event2.setCreatedAt(LocalDateTime.now());
        auditTrailRepository.save(event2);
    }
}
