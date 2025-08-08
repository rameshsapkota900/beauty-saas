package com.example.beautysaas.controller;

import com.example.beautysaas.dto.security.*;
import com.example.beautysaas.service.SecurityChallengeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SecurityChallengeController.class)
class SecurityChallengeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SecurityChallengeService securityChallengeService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final Long TEST_ID = 1L;

    @BeforeEach
    void setUp() {
        // Set up common test data
    }

    @Test
    void getUserRiskAssessment_Success() throws Exception {
        SecurityRiskAssessmentDTO assessment = new SecurityRiskAssessmentDTO();
        assessment.setEmail(TEST_EMAIL);
        assessment.setCurrentRiskScore(0.3);
        assessment.setSecurityLevel("MEDIUM");

        when(securityChallengeService.getUserRiskAssessment(TEST_EMAIL))
            .thenReturn(assessment);

        mockMvc.perform(get("/api/v1/security/challenges/risk/{email}", TEST_EMAIL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(TEST_EMAIL))
            .andExpect(jsonPath("$.currentRiskScore").value(0.3))
            .andExpect(jsonPath("$.securityLevel").value("MEDIUM"));
    }

    @Test
    void updateDeviceInfo_Success() throws Exception {
        DeviceInfoRequest request = new DeviceInfoRequest();
        request.setDeviceFingerprint("device-123");
        request.setGeolocation("40.7128,-74.0060");
        
        SecurityChallengeResponse response = new SecurityChallengeResponse();
        response.setSuccess(true);
        
        when(securityChallengeService.updateDeviceInfo(eq(TEST_ID), any(DeviceInfoRequest.class)))
            .thenReturn(response);

        mockMvc.perform(post("/api/v1/security/challenges/{id}/device-info", TEST_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getUserSecurityLevel_Success() throws Exception {
        SecurityLevelDTO securityLevel = new SecurityLevelDTO();
        securityLevel.setEmail(TEST_EMAIL);
        securityLevel.setCurrentLevel("HIGH");
        securityLevel.setRecommendedLevel("CRITICAL");
        
        when(securityChallengeService.getUserSecurityLevel(TEST_EMAIL))
            .thenReturn(securityLevel);

        mockMvc.perform(get("/api/v1/security/challenges/security-level/{email}", TEST_EMAIL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(TEST_EMAIL))
            .andExpect(jsonPath("$.currentLevel").value("HIGH"))
            .andExpect(jsonPath("$.recommendedLevel").value("CRITICAL"));
    }

    @Test
    void updateSecurityLevel_Success() throws Exception {
        SecurityLevelUpdateRequest request = new SecurityLevelUpdateRequest();
        request.setSecurityLevel("HIGH");
        request.setReason("Suspicious activity detected");
        
        SecurityChallengeResponse response = new SecurityChallengeResponse();
        response.setSuccess(true);
        
        when(securityChallengeService.updateSecurityLevel(eq(TEST_ID), any(SecurityLevelUpdateRequest.class)))
            .thenReturn(response);

        mockMvc.perform(post("/api/v1/security/challenges/{id}/security-level", TEST_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getUserSecurityStats_Success() throws Exception {
        SecurityStatsDTO stats = new SecurityStatsDTO();
        stats.setEmail(TEST_EMAIL);
        stats.setTotalChallenges(10);
        stats.setSuccessfulChallenges(8);
        stats.setFailedChallenges(2);
        stats.setSuccessRate(0.8);
        
        when(securityChallengeService.getUserSecurityStats(TEST_EMAIL))
            .thenReturn(stats);

        mockMvc.perform(get("/api/v1/security/challenges/stats/{email}", TEST_EMAIL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(TEST_EMAIL))
            .andExpect(jsonPath("$.totalChallenges").value(10))
            .andExpect(jsonPath("$.successRate").value(0.8));
    }

    @Test
    void resetAttempts_Success() throws Exception {
        SecurityChallengeResponse response = new SecurityChallengeResponse();
        response.setSuccess(true);
        
        when(securityChallengeService.resetAttempts(TEST_ID))
            .thenReturn(response);

        mockMvc.perform(post("/api/v1/security/challenges/{id}/reset-attempts", TEST_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }
}
