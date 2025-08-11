package com.example.beautysaas.service;

import com.example.beautysaas.entity.TwoFactorSecret;
import com.example.beautysaas.repository.TwoFactorSecretRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TwoFactorAuthService {
    
    private final TwoFactorSecretRepository secretRepository;
    private final TotpService totpService;
    private final SecurityEventNotifierService eventNotifier;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${security.2fa.secret-length:32}")
    private int secretLength;
    
    @Value("${security.2fa.code-validity-minutes:5}")
    private int codeValidityMinutes;
    
    /**
     * Enable 2FA for a user
     */
    @Transactional
    public String enable2FA(String email) {
        // Generate a new secret
        String secret = generateSecret();
        
        // Save or update the secret
        TwoFactorSecret twoFactorSecret = secretRepository.findByEmail(email)
                .orElse(TwoFactorSecret.builder()
                        .email(email)
                        .enabled(true)
                        .build());
        
        twoFactorSecret.setSecret(secret);
        twoFactorSecret.setEnabled(true);
        secretRepository.save(twoFactorSecret);
        
        log.info("2FA enabled for user: {}", email);
        eventNotifier.notifySecurityEvent("2FA_ENABLED", email, "Two-factor authentication enabled");
        
        return secret;
    }
    
    /**
     * Disable 2FA for a user
     */
    @Transactional
    public void disable2FA(String email) {
        secretRepository.findByEmail(email).ifPresent(secret -> {
            secret.setEnabled(false);
            secretRepository.save(secret);
            
            log.info("2FA disabled for user: {}", email);
            eventNotifier.notifySecurityEvent("2FA_DISABLED", email, "Two-factor authentication disabled");
        });
    }
    
    /**
     * Verify a 2FA code
     */
    public boolean verifyCode(String email, String code) {
        return secretRepository.findByEmail(email)
                .filter(TwoFactorSecret::isEnabled)
                .map(secret -> totpService.verifyCode(secret.getSecret(), code))
                .orElse(false);
    }
    
    /**
     * Check if 2FA is enabled for a user
     */
    public boolean is2FAEnabled(String email) {
        return secretRepository.findByEmail(email)
                .map(TwoFactorSecret::isEnabled)
                .orElse(false);
    }
    
    /**
     * Generate a new secret
     */
    private String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[secretLength];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    /**
     * Generate backup codes for a user
     */
    @Transactional
    public List<String> generateBackupCodes(String email) {
        List<String> backupCodes = new ArrayList<>();
        SecureRandom random = new SecureRandom();
        
        // Generate 8 backup codes
        for (int i = 0; i < 8; i++) {
            String code = String.format("%08d", random.nextInt(100000000));
            backupCodes.add(code);
        }
        
        // Save hashed backup codes
        TwoFactorSecret secret = secretRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("2FA not enabled for user"));
        
        secret.setBackupCodes(backupCodes.stream()
                .map(code -> passwordEncoder.encode(code))
                .collect(Collectors.toList()));
        
        secretRepository.save(secret);
        
        return backupCodes;
    }
}
