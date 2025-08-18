package com.example.beautysaas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordHistoryService {
    
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${security.password.history.size:5}")
    private int passwordHistorySize;
    
    @Value("${security.password.min-age-hours:24}")
    private int minimumPasswordAgeHours;
    
    /**
     * Check if the password was used recently
     */
    public boolean isPasswordPreviouslyUsed(String email, String newPassword) {
        List<PasswordHistory> history = passwordHistoryRepository.findByEmailOrderByCreatedAtDesc(email);
        
        return history.stream()
                     .anyMatch(ph -> passwordEncoder.matches(newPassword, ph.getPasswordHash()));
    }
    
    /**
     * Save password to history
     */
    @Transactional
    public void savePassword(String email, String password) {
        // Remove old entries if we exceed history size
        List<PasswordHistory> history = passwordHistoryRepository.findByEmailOrderByCreatedAtDesc(email);
        if (history.size() >= passwordHistorySize) {
            passwordHistoryRepository.deleteAll(
                history.subList(passwordHistorySize - 1, history.size())
            );
        }
        
        // Save new password history entry
        PasswordHistory entry = PasswordHistory.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .createdAt(LocalDateTime.now())
                .build();
        
        passwordHistoryRepository.save(entry);
        log.info("Password history updated for user: {}", email);
    }
    
    /**
     * Check if password can be changed (minimum age requirement)
     */
    public boolean canChangePassword(String email) {
        return passwordHistoryRepository.findFirstByEmailOrderByCreatedAtDesc(email)
                .map(lastChange -> 
                    lastChange.getCreatedAt()
                             .plusHours(minimumPasswordAgeHours)
                             .isBefore(LocalDateTime.now())
                )
                .orElse(true);
    }
    
    /**
     * Get password age in days
     */
    public long getPasswordAge(String email) {
        return passwordHistoryRepository.findFirstByEmailOrderByCreatedAtDesc(email)
                .map(lastChange -> 
                    java.time.Duration.between(
                        lastChange.getCreatedAt(),
                        LocalDateTime.now()
                    ).toDays()
                )
                .orElse(0L);
    }
    
    /**
     * Clean up old password history entries
     */
    @Transactional
    public void cleanupOldHistory() {
        LocalDateTime threshold = LocalDateTime.now().minusYears(1);
        passwordHistoryRepository.deleteByCreatedAtBefore(threshold);
        log.info("Cleaned up password history older than 1 year");
    }
}
