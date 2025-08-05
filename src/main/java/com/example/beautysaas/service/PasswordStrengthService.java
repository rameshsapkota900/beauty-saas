package com.example.beautysaas.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
@Slf4j
public class PasswordStrengthService {
    private static final Pattern HAS_UPPER = Pattern.compile("[A-Z]");
    private static final Pattern HAS_LOWER = Pattern.compile("[a-z]");
    private static final Pattern HAS_NUMBER = Pattern.compile("\\d");
    private static final Pattern HAS_SPECIAL = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
    
    public enum PasswordStrength {
        VERY_WEAK,
        WEAK,
        MODERATE,
        STRONG,
        VERY_STRONG
    }
    
    public PasswordStrength calculateStrength(String password) {
        if (password == null || password.length() < 8) {
            return PasswordStrength.VERY_WEAK;
        }
        
        int score = 0;
        
        // Length checks
        if (password.length() >= 12) score += 2;
        else if (password.length() >= 10) score += 1;
        
        // Character variety checks
        if (HAS_UPPER.matcher(password).find()) score += 1;
        if (HAS_LOWER.matcher(password).find()) score += 1;
        if (HAS_NUMBER.matcher(password).find()) score += 1;
        if (HAS_SPECIAL.matcher(password).find()) score += 2;
        
        // Sequential character check
        if (!hasSequentialChars(password)) score += 1;
        
        // Return strength based on score
        if (score >= 7) return PasswordStrength.VERY_STRONG;
        if (score >= 6) return PasswordStrength.STRONG;
        if (score >= 4) return PasswordStrength.MODERATE;
        if (score >= 2) return PasswordStrength.WEAK;
        return PasswordStrength.VERY_WEAK;
    }
    
    private boolean hasSequentialChars(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);
            
            if (isSequential(c1, c2, c3)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isSequential(char c1, char c2, char c3) {
        return (c1 + 1 == c2 && c2 + 1 == c3) || // ascending
               (c1 - 1 == c2 && c2 - 1 == c3);    // descending
    }
}
