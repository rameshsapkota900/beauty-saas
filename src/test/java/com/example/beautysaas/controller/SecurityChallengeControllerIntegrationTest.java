package com.example.beautysaas.controller;

import com.example.beautysaas.dto.security.*;
import com.example.beautysaas.entity.SecurityChallenge;
import com.example.beautysaas.repository.SecurityChallengeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityChallengeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityChallengeRepository securityChallengeRepository;

    private SecurityChallengeRequest challengeRequest;
    private SecurityChallengeVerifyRequest verifyRequest;

    @BeforeEach
    void setUp() {
        challengeRequest = new SecurityChallengeRequest();
        challengeRequest.setEmail("test@example.com");
        challengeRequest.setChallengeType("EMAIL_VERIFICATION");
        challengeRequest.setChallengeData("test data");
        challengeRequest.setIpAddress("127.0.0.1");
        challengeRequest.setUserAgent("Mozilla/5.0");

        verifyRequest = new SecurityChallengeVerifyRequest();
        verifyRequest.setChallengeId(1L);
        verifyRequest.setVerificationToken("test-token");
        verifyRequest.setAnswer("test-answer");
    }

    @Test
    void createChallenge_Success() throws Exception {
        mockMvc.perform(post("/api/v1/security/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(challengeRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.challengeId").exists());
    }

    @Test
    void createChallenge_DuplicateActive() throws Exception {
        // Create first challenge
        mockMvc.perform(post("/api/v1/security/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(challengeRequest)))
            .andExpect(status().isOk());

        // Attempt to create duplicate
        mockMvc.perform(post("/api/v1/security/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(challengeRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void verifyChallenge_Success() throws Exception {
        // Create challenge
        String response = mockMvc.perform(post("/api/v1/security/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(challengeRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        SecurityChallengeResponse challengeResponse = 
            objectMapper.readValue(response, SecurityChallengeResponse.class);

        // Set up verification request
        verifyRequest.setChallengeId(challengeResponse.getChallengeId());
        SecurityChallenge challenge = securityChallengeRepository.findById(challengeResponse.getChallengeId())
            .orElseThrow();
        verifyRequest.setVerificationToken(challenge.getVerificationToken());

        // Verify challenge
        mockMvc.perform(post("/api/v1/security/challenges/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getUserActiveChallenge_Success() throws Exception {
        // Create challenge
        mockMvc.perform(post("/api/v1/security/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(challengeRequest)))
            .andExpect(status().isOk());

        // Get active challenge
        mockMvc.perform(get("/api/v1/security/challenges/user/{email}", "test@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.challengeType").value("EMAIL_VERIFICATION"));
    }

    @Test
    void getUserActiveChallenge_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/security/challenges/user/{email}", "nonexistent@example.com"))
            .andExpect(status().isNotFound());
    }

    @Test
    void invalidateChallenge_Success() throws Exception {
        // Create challenge
        String response = mockMvc.perform(post("/api/v1/security/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(challengeRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        SecurityChallengeResponse challengeResponse = 
            objectMapper.readValue(response, SecurityChallengeResponse.class);

        // Invalidate challenge
        mockMvc.perform(delete("/api/v1/security/challenges/{id}", challengeResponse.getChallengeId()))
            .andExpect(status().isNoContent());

        // Verify challenge is invalidated
        SecurityChallenge challenge = securityChallengeRepository.findById(challengeResponse.getChallengeId())
            .orElseThrow();
        assertTrue(challenge.isExpired());
    }
}
