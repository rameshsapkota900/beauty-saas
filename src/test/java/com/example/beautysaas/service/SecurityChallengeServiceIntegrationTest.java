package com.example.beautysaas.service;

import com.example.beautysaas.dto.security.*;
import com.example.beautysaas.entity.SecurityChallenge;
import com.example.beautysaas.repository.SecurityChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SecurityChallengeServiceIntegrationTest {

    @Autowired
    private SecurityChallengeService securityChallengeService;

    @Autowired
    private SecurityChallengeRepository securityChallengeRepository;

    private static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        securityChallengeRepository.deleteAll();
    }

    @Test
    void createAndVerifyChallenge_Success() {
        // Create challenge
        SecurityChallengeRequest createRequest = new SecurityChallengeRequest();
        createRequest.setEmail(TEST_EMAIL);
        createRequest.setChallengeType("EMAIL_VERIFICATION");
        createRequest.setChallengeData("test-data");
        createRequest.setIpAddress("127.0.0.1");

        SecurityChallengeResponse createResponse = securityChallengeService.createChallenge(createRequest);
        assertNotNull(createResponse.getChallengeId());

        // Get challenge details
        SecurityChallenge challenge = securityChallengeRepository.findById(createResponse.getChallengeId())
            .orElseThrow();

        // Verify challenge
        SecurityChallengeVerifyRequest verifyRequest = new SecurityChallengeVerifyRequest();
        verifyRequest.setChallengeId(challenge.getId());
        verifyRequest.setVerificationToken(challenge.getVerificationToken());

        SecurityChallengeResponse verifyResponse = securityChallengeService.verifyChallenge(verifyRequest);
        assertTrue(verifyResponse.getSuccess());
    }

    @Test
    void getUserRiskAssessment_WithHistory() {
        // Create multiple challenges with different risk levels
        createChallengeWithRisk(TEST_EMAIL, 0.3);
        createChallengeWithRisk(TEST_EMAIL, 0.7);
        createChallengeWithRisk(TEST_EMAIL, 0.5);

        SecurityRiskAssessmentDTO assessment = securityChallengeService.getUserRiskAssessment(TEST_EMAIL);
        
        assertNotNull(assessment);
        assertEquals(TEST_EMAIL, assessment.getEmail());
        assertTrue(assessment.getCurrentRiskScore() > 0);
        assertNotNull(assessment.getSecurityLevel());
    }

    @Test
    void updateDeviceInfo_Success() {
        // Create initial challenge
        SecurityChallenge challenge = createBasicChallenge(TEST_EMAIL);

        DeviceInfoRequest request = new DeviceInfoRequest();
        request.setDeviceFingerprint("device-123");
        request.setGeolocation("40.7128,-74.0060");
        request.setDeviceType("Mobile");
        request.setOperatingSystem("iOS");

        SecurityChallengeResponse response = securityChallengeService.updateDeviceInfo(challenge.getId(), request);
        assertTrue(response.getSuccess());

        SecurityChallenge updated = securityChallengeRepository.findById(challenge.getId()).orElseThrow();
        assertEquals("device-123", updated.getDeviceFingerprint());
        assertEquals("40.7128,-74.0060", updated.getGeolocation());
    }

    @Test
    void getUserSecurityLevel_Progression() {
        // Create challenges with increasing risk
        createChallengeWithRisk(TEST_EMAIL, 0.2); // LOW
        createChallengeWithRisk(TEST_EMAIL, 0.5); // MEDIUM
        createChallengeWithRisk(TEST_EMAIL, 0.7); // HIGH

        SecurityLevelDTO securityLevel = securityChallengeService.getUserSecurityLevel(TEST_EMAIL);
        
        assertNotNull(securityLevel);
        assertEquals(TEST_EMAIL, securityLevel.getEmail());
        assertEquals("HIGH", securityLevel.getCurrentLevel());
        assertTrue(securityLevel.getRequiresStepUp());
    }

    @Test
    void getUserSecurityStats_ComprehensiveAnalysis() {
        // Create a mix of successful and failed challenges
        createSuccessfulChallenge(TEST_EMAIL);
        createFailedChallenge(TEST_EMAIL, "Invalid token");
        createSuccessfulChallenge(TEST_EMAIL);
        createFailedChallenge(TEST_EMAIL, "Expired");

        SecurityStatsDTO stats = securityChallengeService.getUserSecurityStats(TEST_EMAIL);
        
        assertNotNull(stats);
        assertEquals(TEST_EMAIL, stats.getEmail());
        assertEquals(4, stats.getTotalChallenges());
        assertEquals(2, stats.getSuccessfulChallenges());
        assertEquals(2, stats.getFailedChallenges());
        assertEquals(0.5, stats.getSuccessRate());
    }

    // Helper methods
    private SecurityChallenge createBasicChallenge(String email) {
        SecurityChallenge challenge = SecurityChallenge.builder()
            .email(email)
            .challengeType(SecurityChallenge.ChallengeType.EMAIL_VERIFICATION)
            .verificationToken("test-token")
            .build();
        return securityChallengeRepository.save(challenge);
    }

    private void createChallengeWithRisk(String email, double riskScore) {
        SecurityChallenge challenge = createBasicChallenge(email);
        challenge.updateRiskScore(riskScore);
        securityChallengeRepository.save(challenge);
    }

    private void createSuccessfulChallenge(String email) {
        SecurityChallenge challenge = createBasicChallenge(email);
        challenge.complete();
        securityChallengeRepository.save(challenge);
    }

    private void createFailedChallenge(String email, String reason) {
        SecurityChallenge challenge = createBasicChallenge(email);
        challenge.fail(reason);
        securityChallengeRepository.save(challenge);
    }
}
