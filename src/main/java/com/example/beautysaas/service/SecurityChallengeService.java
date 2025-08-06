package com.example.beautysaas.service;

import com.example.beautysaas.entity.SecurityChallenge;
import com.example.beautysaas.repository.SecurityChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityChallengeService {

    private final SecurityChallengeRepository challengeRepository;
    private final SecurityService securityService;

    @Value("${security.challenge.max-attempts:3}")
    private int maxAttempts;

    @Value("${security.challenge.expiry-minutes:30}")
    private int challengeExpiryMinutes;

    /**
     * Create a new security challenge
     */
    @Transactional
    public SecurityChallenge createChallenge(String email, SecurityChallenge.ChallengeType type,
                                           String ipAddress, String userAgent) {
        // Check for existing active challenge
        Optional<SecurityChallenge> existingChallenge = challengeRepository
                .findByEmailAndChallengeTypeAndIsCompletedFalseAndExpiresAtAfter(
                        email, type, LocalDateTime.now());

        if (existingChallenge.isPresent()) {
            return existingChallenge.get();
        }

        // Create new challenge
        SecurityChallenge challenge = SecurityChallenge.builder()
                .email(email)
                .challengeType(type)
                .verificationToken(generateVerificationToken())
                .expiresAt(LocalDateTime.now().plusMinutes(challengeExpiryMinutes))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        challengeRepository.save(challenge);

        // Log security event
        securityService.logSecurityEvent(email, "SECURITY_CHALLENGE_CREATED",
                ipAddress, userAgent, "Security challenge created: " + type, true);

        return challenge;
    }

    /**
     * Verify challenge response
     */
    @Transactional
    public boolean verifyChallenge(String token, String response, String ipAddress) {
        Optional<SecurityChallenge> challengeOpt = challengeRepository.findByVerificationToken(token);
        
        if (challengeOpt.isEmpty()) {
            log.warn("Security challenge not found for token: {}", token);
            return false;
        }

        SecurityChallenge challenge = challengeOpt.get();

        // Check if challenge is expired
        if (challenge.isExpired()) {
            securityService.logSecurityEvent(challenge.getEmail(), "SECURITY_CHALLENGE_EXPIRED",
                    ipAddress, challenge.getUserAgent(), 
                    "Expired security challenge attempted: " + challenge.getChallengeType(), false);
            return false;
        }

        // Check if challenge is already completed
        if (challenge.getIsCompleted()) {
            securityService.logSecurityEvent(challenge.getEmail(), "SECURITY_CHALLENGE_REUSE_ATTEMPT",
                    ipAddress, challenge.getUserAgent(),
                    "Attempt to reuse completed challenge: " + challenge.getChallengeType(), false);
            return false;
        }

        // Increment attempt counter
        challenge.incrementAttempts();

        // Verify the response (implement specific verification logic based on challenge type)
        boolean isValid = validateChallengeResponse(challenge, response);

        if (isValid) {
            challenge.complete();
            securityService.logSecurityEvent(challenge.getEmail(), "SECURITY_CHALLENGE_COMPLETED",
                    ipAddress, challenge.getUserAgent(),
                    "Security challenge completed successfully: " + challenge.getChallengeType(), true);
        } else {
            securityService.logSecurityEvent(challenge.getEmail(), "SECURITY_CHALLENGE_FAILED",
                    ipAddress, challenge.getUserAgent(),
                    "Security challenge failed: " + challenge.getChallengeType(), false);
            
            if (challenge.getAttemptCount() >= maxAttempts) {
                securityService.logSecurityEvent(challenge.getEmail(), "SECURITY_CHALLENGE_MAX_ATTEMPTS",
                        ipAddress, challenge.getUserAgent(),
                        "Maximum attempts reached for security challenge: " + challenge.getChallengeType(), false);
            }
        }

        challengeRepository.save(challenge);
        return isValid;
    }

    /**
     * Check if user has any active challenges
     */
    public boolean hasActiveChallenges(String email) {
        return !challengeRepository.findActiveByEmail(email, LocalDateTime.now()).isEmpty();
    }

    /**
     * Get active challenges for a user
     */
    public List<SecurityChallenge> getActiveChallenges(String email) {
        return challengeRepository.findActiveByEmail(email, LocalDateTime.now());
    }

    /**
     * Clean up expired challenges
     */
    @Scheduled(fixedRate = 900000) // Every 15 minutes
    @Transactional
    public void cleanupExpiredChallenges() {
        LocalDateTime expiryTime = LocalDateTime.now();
        List<SecurityChallenge> expiredChallenges = challengeRepository.findExpiredChallenges(expiryTime);
        
        for (SecurityChallenge challenge : expiredChallenges) {
            if (!challenge.getIsCompleted()) {
                securityService.logSecurityEvent(challenge.getEmail(), "SECURITY_CHALLENGE_EXPIRED",
                        challenge.getIpAddress(), challenge.getUserAgent(),
                        "Security challenge expired: " + challenge.getChallengeType(), false);
            }
        }
        
        log.debug("Cleaned up {} expired security challenges", expiredChallenges.size());
    }

    /**
     * Generate unique verification token
     */
    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Validate challenge response based on challenge type
     */
    private boolean validateChallengeResponse(SecurityChallenge challenge, String response) {
        switch (challenge.getChallengeType()) {
            case SECURITY_QUESTION:
                return validateSecurityQuestionResponse(challenge, response);
            case CAPTCHA:
                return validateCaptchaResponse(response);
            case EMAIL_VERIFICATION:
                return validateEmailVerificationResponse(challenge, response);
            case PHONE_VERIFICATION:
                return validatePhoneVerificationResponse(challenge, response);
            case ADMIN_APPROVAL:
                return validateAdminApprovalResponse(challenge, response);
            case RISK_BASED:
                return validateRiskBasedResponse(challenge, response);
            default:
                log.warn("Unsupported challenge type: {}", challenge.getChallengeType());
                return false;
        }
    }

    private boolean validateSecurityQuestionResponse(SecurityChallenge challenge, String response) {
        // TODO: Implement security question validation logic
        return false;
    }

    private boolean validateCaptchaResponse(String response) {
        // TODO: Implement CAPTCHA validation logic
        return false;
    }

    private boolean validateEmailVerificationResponse(SecurityChallenge challenge, String response) {
        return challenge.getVerificationToken().equals(response);
    }

    private boolean validatePhoneVerificationResponse(SecurityChallenge challenge, String response) {
        // TODO: Implement phone verification logic
        return false;
    }

    private boolean validateAdminApprovalResponse(SecurityChallenge challenge, String response) {
        // TODO: Implement admin approval validation logic
        return false;
    }

    private boolean validateRiskBasedResponse(SecurityChallenge challenge, String response) {
        // TODO: Implement risk-based challenge validation logic
        return false;
    }
}
