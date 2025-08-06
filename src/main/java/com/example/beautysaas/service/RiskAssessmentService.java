package com.example.beautysaas.service;

import com.example.beautysaas.entity.RiskAssessment;
import com.example.beautysaas.entity.SecurityChallenge;
import com.example.beautysaas.repository.RiskAssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskAssessmentService {

    private final RiskAssessmentRepository riskRepository;
    private final SecurityService securityService;
    private final SecurityChallengeService challengeService;
    private final IpGeolocationService geoLocationService;

    @Value("${security.risk.high-risk-threshold:60.0}")
    private double highRiskThreshold;

    @Value("${security.risk.critical-risk-threshold:80.0}")
    private double criticalRiskThreshold;

    /**
     * Assess risk for a user action
     */
    @Transactional
    public RiskAssessment assessRisk(String email, String ipAddress, String userAgent, 
                                   RiskAssessment.AssessmentType type) {
        // Calculate risk score based on various factors
        Map<String, Double> riskFactors = new HashMap<>();
        List<String> recommendedActions = new ArrayList<>();

        // Check location-based risk
        double locationRisk = assessLocationRisk(email, ipAddress);
        if (locationRisk > 0) {
            riskFactors.put("Location Risk", locationRisk);
        }

        // Check time-based patterns
        double timeRisk = assessTimeBasedRisk(email);
        if (timeRisk > 0) {
            riskFactors.put("Time Pattern Risk", timeRisk);
        }

        // Check user behavior patterns
        double behaviorRisk = assessBehaviorRisk(email, type);
        if (behaviorRisk > 0) {
            riskFactors.put("Behavior Risk", behaviorRisk);
        }

        // Check for recent security incidents
        double incidentRisk = assessSecurityIncidentRisk(email);
        if (incidentRisk > 0) {
            riskFactors.put("Security Incident Risk", incidentRisk);
        }

        // Calculate overall risk score
        double riskScore = calculateOverallRiskScore(riskFactors);
        RiskAssessment.RiskLevel riskLevel = RiskAssessment.calculateRiskLevel(riskScore);

        // Determine recommended actions
        recommendedActions.addAll(determineRecommendedActions(riskLevel, type));

        // Create risk assessment record
        RiskAssessment assessment = RiskAssessment.builder()
                .email(email)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .riskScore(riskScore)
                .riskLevel(riskLevel)
                .assessmentType(type)
                .riskFactors(mapToString(riskFactors))
                .recommendedActions(String.join(", ", recommendedActions))
                .build();

        riskRepository.save(assessment);

        // Log high-risk assessments
        if (riskLevel.ordinal() >= RiskAssessment.RiskLevel.HIGH.ordinal()) {
            securityService.logSecurityEvent(email, "HIGH_RISK_ASSESSMENT", ipAddress, userAgent,
                    String.format("High risk activity detected (Score: %.2f, Type: %s)", riskScore, type), false);
            
            // Create security challenge for high-risk activities
            if (!challengeService.hasActiveChallenges(email)) {
                challengeService.createChallenge(email, SecurityChallenge.ChallengeType.RISK_BASED,
                        ipAddress, userAgent);
            }
        }

        return assessment;
    }

    /**
     * Assess location-based risk
     */
    private double assessLocationRisk(String email, String ipAddress) {
        double risk = 0.0;

        // Check for location change
        if (geoLocationService.isSuspiciousLocationChange(email, ipAddress)) {
            risk += 30.0;
        }

        // Check if location is known for suspicious activity
        // TODO: Implement location reputation check

        return risk;
    }

    /**
     * Assess time-based risk patterns
     */
    private double assessTimeBasedRisk(String email) {
        double risk = 0.0;
        LocalDateTime now = LocalDateTime.now();

        // Check for unusual activity times
        int hour = now.getHour();
        if (hour >= 0 && hour <= 4) { // Suspicious hours
            risk += 20.0;
        }

        // Check for rapid successive actions
        LocalDateTime recent = now.minusMinutes(5);
        List<RiskAssessment> recentAssessments = riskRepository.findRecentAssessments(email, recent);
        
        if (recentAssessments.size() > 10) { // Too many actions in short time
            risk += 25.0;
        }

        return risk;
    }

    /**
     * Assess user behavior risk
     */
    private double assessBehaviorRisk(String email, RiskAssessment.AssessmentType type) {
        double risk = 0.0;

        // Check recent high-risk assessments
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        long highRiskCount = riskRepository.countHighRiskAssessments(email, dayAgo);
        
        if (highRiskCount > 0) {
            risk += 20.0 * highRiskCount;
        }

        // Add type-specific risk scores
        switch (type) {
            case PAYMENT_TRANSACTION:
                risk += 25.0; // Higher baseline risk for financial transactions
                break;
            case ADMIN_ACTION:
                risk += 20.0; // Higher baseline for administrative actions
                break;
            case PROFILE_UPDATE:
                risk += 15.0; // Moderate risk for profile changes
                break;
            default:
                break;
        }

        return risk;
    }

    /**
     * Assess risk based on recent security incidents
     */
    private double assessSecurityIncidentRisk(String email) {
        double risk = 0.0;

        // Check for recent failed login attempts
        // TODO: Implement failed login check

        // Check for recent security challenges
        if (challengeService.hasActiveChallenges(email)) {
            risk += 25.0;
        }

        return risk;
    }

    /**
     * Calculate overall risk score from individual factors
     */
    private double calculateOverallRiskScore(Map<String, Double> riskFactors) {
        if (riskFactors.isEmpty()) {
            return 0.0;
        }

        return riskFactors.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum() / riskFactors.size();
    }

    /**
     * Determine recommended actions based on risk level
     */
    private List<String> determineRecommendedActions(RiskAssessment.RiskLevel riskLevel,
                                                   RiskAssessment.AssessmentType type) {
        List<String> actions = new ArrayList<>();

        switch (riskLevel) {
            case CRITICAL:
                actions.add("Block account access");
                actions.add("Notify security team");
                actions.add("Force password reset");
                actions.add("Require admin verification");
                break;
            case HIGH:
                actions.add("Require additional authentication");
                actions.add("Send security notification");
                actions.add("Monitor account activity");
                break;
            case MEDIUM:
                actions.add("Request email verification");
                actions.add("Monitor for escalation");
                break;
            case LOW:
                actions.add("Normal processing");
                break;
        }

        return actions;
    }

    /**
     * Convert risk factors map to string representation
     */
    private String mapToString(Map<String, Double> map) {
        return map.entrySet().stream()
                .map(e -> String.format("%s: %.2f", e.getKey(), e.getValue()))
                .collect(Collectors.joining(", "));
    }

    /**
     * Get unresolved high-risk assessments
     */
    public List<RiskAssessment> getUnresolvedHighRiskAssessments() {
        return riskRepository.findUnresolvedHighRiskAssessments();
    }

    /**
     * Get users with critical risk level
     */
    public List<String> getUsersWithCriticalRisk() {
        return riskRepository.findUsersWithCriticalRisk(LocalDateTime.now().minusDays(1));
    }

    /**
     * Monitor and alert on critical risk assessments
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    @Transactional
    public void monitorCriticalRisks() {
        List<String> criticalRiskUsers = getUsersWithCriticalRisk();
        
        for (String email : criticalRiskUsers) {
            securityService.logSecurityEvent(email, "CRITICAL_RISK_ALERT", null, null,
                    "User has critical risk level - immediate attention required", false);
            // TODO: Implement notification system for security team
        }
    }
}
