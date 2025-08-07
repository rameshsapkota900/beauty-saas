package com.example.beautysaas.util;

import com.example.beautysaas.entity.SecurityChallenge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityRiskAnalyzerTest {

    private SecurityRiskAnalyzer riskAnalyzer;
    private SecurityChallenge challenge;

    @BeforeEach
    void setUp() {
        riskAnalyzer = new SecurityRiskAnalyzer();
        challenge = SecurityChallenge.builder()
            .email("test@example.com")
            .challengeType(SecurityChallenge.ChallengeType.EMAIL_VERIFICATION)
            .deviceFingerprint("test-device")
            .geolocation("test-location")
            .attemptCount(0)
            .build();
    }

    @Test
    void calculateRiskScore_NewDevice() {
        challenge.setDeviceFingerprint("unknown-device");
        double riskScore = riskAnalyzer.calculateRiskScore(challenge);
        assertTrue(riskScore > 0.3, "Risk score should be elevated for unknown device");
    }

    @Test
    void calculateRiskScore_KnownDevice() {
        // Record a successful challenge with this device
        riskAnalyzer.recordChallengeOutcome(challenge, true);
        
        // Calculate risk score for same device
        double riskScore = riskAnalyzer.calculateRiskScore(challenge);
        assertTrue(riskScore <= 0.3, "Risk score should be lower for known device");
    }

    @Test
    void calculateRiskScore_NewLocation() {
        challenge.setGeolocation("unknown-location");
        double riskScore = riskAnalyzer.calculateRiskScore(challenge);
        assertTrue(riskScore > 0.3, "Risk score should be elevated for unknown location");
    }

    @Test
    void calculateRiskScore_HighRiskHours() {
        // Simulate early morning hours
        challenge.setLastAttemptTime(LocalDateTime.now().withHour(3));
        double riskScore = riskAnalyzer.calculateRiskScore(challenge);
        assertTrue(riskScore > 0.5, "Risk score should be high during suspicious hours");
    }

    @Test
    void calculateRiskScore_RapidAttempts() {
        challenge.setLastAttemptTime(LocalDateTime.now());
        challenge.setAttemptCount(3);
        double riskScore = riskAnalyzer.calculateRiskScore(challenge);
        assertTrue(riskScore > 0.6, "Risk score should be high for rapid attempts");
    }

    @Test
    void determineSecurityLevel_RiskBasedProgression() {
        assertEquals(SecurityChallenge.SecurityLevel.LOW, 
            riskAnalyzer.determineSecurityLevel(0.2));
        assertEquals(SecurityChallenge.SecurityLevel.MEDIUM, 
            riskAnalyzer.determineSecurityLevel(0.45));
        assertEquals(SecurityChallenge.SecurityLevel.HIGH, 
            riskAnalyzer.determineSecurityLevel(0.65));
        assertEquals(SecurityChallenge.SecurityLevel.CRITICAL, 
            riskAnalyzer.determineSecurityLevel(0.85));
    }

    @Test
    void determineChallengeType_RiskBasedEscalation() {
        assertEquals(SecurityChallenge.ChallengeType.CAPTCHA, 
            riskAnalyzer.determineChallengeType(0.2));
        assertEquals(SecurityChallenge.ChallengeType.EMAIL_VERIFICATION, 
            riskAnalyzer.determineChallengeType(0.45));
        assertEquals(SecurityChallenge.ChallengeType.PHONE_VERIFICATION, 
            riskAnalyzer.determineChallengeType(0.65));
        assertEquals(SecurityChallenge.ChallengeType.ADMIN_APPROVAL, 
            riskAnalyzer.determineChallengeType(0.85));
    }

    @Test
    void riskProfile_LearnsFromSuccesses() {
        // Initial risk score for unknown user
        double initialScore = riskAnalyzer.calculateRiskScore(challenge);

        // Record several successful attempts
        for (int i = 0; i < 5; i++) {
            riskAnalyzer.recordChallengeOutcome(challenge, true);
        }

        // Risk score should decrease
        double finalScore = riskAnalyzer.calculateRiskScore(challenge);
        assertTrue(finalScore < initialScore, 
            "Risk score should decrease after successful attempts");
    }

    @Test
    void riskProfile_IncreasesWithFailures() {
        double initialScore = riskAnalyzer.calculateRiskScore(challenge);

        // Record failed attempts
        for (int i = 0; i < 3; i++) {
            riskAnalyzer.recordChallengeOutcome(challenge, false);
        }

        double finalScore = riskAnalyzer.calculateRiskScore(challenge);
        assertTrue(finalScore > initialScore, 
            "Risk score should increase after failed attempts");
    }
}
