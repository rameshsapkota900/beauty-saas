package com.example.beautysaas.util;

import com.example.beautysaas.entity.SecurityChallenge;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SecurityRiskAnalyzer {
    private final Map<String, UserRiskProfile> userRiskProfiles = new ConcurrentHashMap<>();
    
    private static class UserRiskProfile {
        private final String email;
        private double baseRiskScore;
        private int failedAttempts;
        private LocalDateTime lastFailure;
        private Map<String, Integer> knownDevices = new ConcurrentHashMap<>();
        private Map<String, Integer> knownLocations = new ConcurrentHashMap<>();
        
        public UserRiskProfile(String email) {
            this.email = email;
            this.baseRiskScore = 0.3; // Start with moderate risk
        }
        
        public void recordFailure() {
            failedAttempts++;
            lastFailure = LocalDateTime.now();
            baseRiskScore = Math.min(1.0, baseRiskScore + 0.1);
        }
        
        public void recordSuccess() {
            failedAttempts = 0;
            baseRiskScore = Math.max(0.1, baseRiskScore - 0.05);
        }
        
        public void recordDevice(String fingerprint) {
            knownDevices.merge(fingerprint, 1, Integer::sum);
        }
        
        public void recordLocation(String location) {
            knownLocations.merge(location, 1, Integer::sum);
        }
        
        public boolean isKnownDevice(String fingerprint) {
            return knownDevices.containsKey(fingerprint);
        }
        
        public boolean isKnownLocation(String location) {
            return knownLocations.containsKey(location);
        }
    }
    
    public double calculateRiskScore(SecurityChallenge challenge) {
        UserRiskProfile profile = userRiskProfiles.computeIfAbsent(
            challenge.getEmail(),
            UserRiskProfile::new
        );
        
        double riskScore = profile.baseRiskScore;
        
        // Check device fingerprint
        if (challenge.getDeviceFingerprint() != null) {
            if (!profile.isKnownDevice(challenge.getDeviceFingerprint())) {
                riskScore += 0.2;
            }
        }
        
        // Check location
        if (challenge.getGeolocation() != null) {
            if (!profile.isKnownLocation(challenge.getGeolocation())) {
                riskScore += 0.15;
            }
        }
        
        // Check time of day (higher risk during unusual hours)
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        if (hour >= 1 && hour <= 5) { // Early morning hours
            riskScore += 0.25;
        }
        
        // Check attempt frequency
        if (challenge.getLastAttemptTime() != null) {
            long minutesSinceLastAttempt = ChronoUnit.MINUTES.between(
                challenge.getLastAttemptTime(),
                LocalDateTime.now()
            );
            if (minutesSinceLastAttempt < 1) {
                riskScore += 0.3; // Rapid attempts are suspicious
            }
        }
        
        // Check attempt count
        if (challenge.getAttemptCount() > 0) {
            riskScore += 0.1 * challenge.getAttemptCount();
        }
        
        return Math.min(1.0, riskScore);
    }
    
    public void recordChallengeOutcome(SecurityChallenge challenge, boolean success) {
        UserRiskProfile profile = userRiskProfiles.computeIfAbsent(
            challenge.getEmail(),
            UserRiskProfile::new
        );
        
        if (success) {
            profile.recordSuccess();
            if (challenge.getDeviceFingerprint() != null) {
                profile.recordDevice(challenge.getDeviceFingerprint());
            }
            if (challenge.getGeolocation() != null) {
                profile.recordLocation(challenge.getGeolocation());
            }
        } else {
            profile.recordFailure();
        }
    }
    
    public SecurityChallenge.SecurityLevel determineSecurityLevel(double riskScore) {
        if (riskScore >= 0.8) {
            return SecurityChallenge.SecurityLevel.CRITICAL;
        } else if (riskScore >= 0.6) {
            return SecurityChallenge.SecurityLevel.HIGH;
        } else if (riskScore >= 0.4) {
            return SecurityChallenge.SecurityLevel.MEDIUM;
        } else {
            return SecurityChallenge.SecurityLevel.LOW;
        }
    }
    
    public SecurityChallenge.ChallengeType determineChallengeType(double riskScore) {
        if (riskScore >= 0.8) {
            return SecurityChallenge.ChallengeType.ADMIN_APPROVAL;
        } else if (riskScore >= 0.6) {
            return SecurityChallenge.ChallengeType.PHONE_VERIFICATION;
        } else if (riskScore >= 0.4) {
            return SecurityChallenge.ChallengeType.EMAIL_VERIFICATION;
        } else {
            return SecurityChallenge.ChallengeType.CAPTCHA;
        }
    }
}
