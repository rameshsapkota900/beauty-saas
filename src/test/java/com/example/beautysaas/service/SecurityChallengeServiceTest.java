package com.example.beautysaas.service;

import com.example.beautysaas.dto.security.*;
import com.example.beautysaas.entity.SecurityChallenge;
import com.example.beautysaas.repository.SecurityChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityChallengeServiceTest {

    @Mock
    private SecurityChallengeRepository securityChallengeRepository;

    @Mock
    private SecurityNotificationService notificationService;

    @InjectMocks
    private SecurityChallengeService securityChallengeService;

    private SecurityChallengeRequest challengeRequest;
    private SecurityChallenge challenge;
    private SecurityChallengeVerifyRequest verifyRequest;

    @BeforeEach
    void setUp() {
        challengeRequest = new SecurityChallengeRequest();
        challengeRequest.setEmail("test@example.com");
        challengeRequest.setChallengeType("EMAIL_VERIFICATION");
        challengeRequest.setChallengeData("test data");
        challengeRequest.setIpAddress("127.0.0.1");
        challengeRequest.setUserAgent("Mozilla/5.0");

        challenge = SecurityChallenge.builder()
            .id(1L)
            .email("test@example.com")
            .challengeType(SecurityChallenge.ChallengeType.EMAIL_VERIFICATION)
            .challengeData("test data")
            .verificationToken("test-token")
            .ipAddress("127.0.0.1")
            .userAgent("Mozilla/5.0")
            .build();

        verifyRequest = new SecurityChallengeVerifyRequest();
        verifyRequest.setChallengeId(1L);
        verifyRequest.setVerificationToken("test-token");
        verifyRequest.setAnswer("test-answer");
    }

    @Test
    void createChallenge_Success() {
        when(securityChallengeRepository.findActiveByEmail(anyString()))
            .thenReturn(Optional.empty());
        when(securityChallengeRepository.save(any(SecurityChallenge.class)))
            .thenReturn(challenge);

        SecurityChallengeResponse response = securityChallengeService.createChallenge(challengeRequest);

        assertNotNull(response);
        assertEquals(1L, response.getChallengeId());
        assertFalse(response.getIsCompleted());
        verify(notificationService).notifySecurityIncident(anyString(), anyString(), anyMap());
    }

    @Test
    void createChallenge_ExistingActiveChallenge() {
        SecurityChallenge activeChallenge = SecurityChallenge.builder()
            .id(2L)
            .email("test@example.com")
            .expiresAt(LocalDateTime.now().plusMinutes(15))
            .build();

        when(securityChallengeRepository.findActiveByEmail(anyString()))
            .thenReturn(Optional.of(activeChallenge));

        assertThrows(IllegalStateException.class, () -> 
            securityChallengeService.createChallenge(challengeRequest));
    }

    @Test
    void verifyChallenge_Success() {
        when(securityChallengeRepository.findById(anyLong()))
            .thenReturn(Optional.of(challenge));

        SecurityChallengeResponse response = securityChallengeService.verifyChallenge(verifyRequest);

        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getCompletedAt());
        verify(securityChallengeRepository).save(any(SecurityChallenge.class));
    }

    @Test
    void verifyChallenge_InvalidToken() {
        when(securityChallengeRepository.findById(anyLong()))
            .thenReturn(Optional.of(challenge));

        verifyRequest.setVerificationToken("invalid-token");
        SecurityChallengeResponse response = securityChallengeService.verifyChallenge(verifyRequest);

        assertNotNull(response);
        assertFalse(response.getSuccess());
        verify(securityChallengeRepository).save(any(SecurityChallenge.class));
    }

    @Test
    void verifyChallenge_ExpiredChallenge() {
        SecurityChallenge expiredChallenge = SecurityChallenge.builder()
            .id(1L)
            .expiresAt(LocalDateTime.now().minusMinutes(1))
            .build();

        when(securityChallengeRepository.findById(anyLong()))
            .thenReturn(Optional.of(expiredChallenge));

        assertThrows(IllegalStateException.class, () ->
            securityChallengeService.verifyChallenge(verifyRequest));
    }

    @Test
    void verifyChallenge_MaxAttemptsExceeded() {
        SecurityChallenge maxAttemptsChallenge = SecurityChallenge.builder()
            .id(1L)
            .attemptCount(3)
            .expiresAt(LocalDateTime.now().plusMinutes(15))
            .build();

        when(securityChallengeRepository.findById(anyLong()))
            .thenReturn(Optional.of(maxAttemptsChallenge));

        assertThrows(IllegalStateException.class, () ->
            securityChallengeService.verifyChallenge(verifyRequest));
    }

    @Test
    void getUserActiveChallenge_Success() {
        when(securityChallengeRepository.findActiveByEmail(anyString()))
            .thenReturn(Optional.of(challenge));

        SecurityChallengeDTO dto = securityChallengeService.getUserActiveChallenge("test@example.com");

        assertNotNull(dto);
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("EMAIL_VERIFICATION", dto.getChallengeType());
    }

    @Test
    void getUserActiveChallenge_NotFound() {
        when(securityChallengeRepository.findActiveByEmail(anyString()))
            .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            securityChallengeService.getUserActiveChallenge("test@example.com"));
    }
}
