package com.example.beautysaas.service;

import com.example.beautysaas.entity.SecurityChallenge;
import com.example.beautysaas.repository.SecurityChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityChallengeCleanupService {
    private final SecurityChallengeRepository securityChallengeRepository;
    
    @Scheduled(cron = "0 */15 * * * *") // Run every 15 minutes
    @Transactional
    public void cleanupExpiredChallenges() {
        log.info("Starting cleanup of expired security challenges");
        try {
            List<SecurityChallenge> expiredChallenges = 
                securityChallengeRepository.findExpiredChallenges(LocalDateTime.now());
            
            if (!expiredChallenges.isEmpty()) {
                for (SecurityChallenge challenge : expiredChallenges) {
                    challenge.setExpiresAt(LocalDateTime.now());
                }
                securityChallengeRepository.saveAll(expiredChallenges);
                log.info("Cleaned up {} expired security challenges", expiredChallenges.size());
            }
        } catch (Exception e) {
            log.error("Error during security challenge cleanup", e);
        }
    }
}
