package com.example.beautysaas.service;

import com.example.beautysaas.dto.auth.PasswordStrengthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordPolicyService {

    private final PasswordHistoryService passwordHistoryService;

    @Value("${security.password.min-length:8}")
    private int minLength;

    @Value("${security.password.require-uppercase:true}")
    private boolean requireUppercase;

    @Value("${security.password.require-lowercase:true}")
    private boolean requireLowercase;

    @Value("${security.password.require-digits:true}")
    private boolean requireDigits;

    @Value("${security.password.require-special-chars:true}")
    private boolean requireSpecialChars;
    
    @Value("${security.password.max-age-days:90}")
    private int maxAgeDays;
    
    @Value("${security.password.min-unique-chars:6}")
    private int minUniqueChars;
    
    @Value("${security.password.history-count:5}")
    private int historyCount;

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");

    /**
     * Validate password against policy
     */
    public PasswordValidationResult validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.length() < minLength) {
            errors.add("Password must be at least " + minLength + " characters long");
        }

        if (requireUppercase && !UPPERCASE_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one uppercase letter");
        }

        if (requireLowercase && !LOWERCASE_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one lowercase letter");
        }

        if (requireDigits && !DIGIT_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one digit");
        }

        if (requireSpecialChars && !SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one special character");
        }

        boolean isValid = errors.isEmpty();
        return new PasswordValidationResult(isValid, errors);
    }

    /**
     * Calculate password strength score (0-100)
     */
    public int calculatePasswordStrength(String password) {
        if (password == null) return 0;

        int score = 0;
        
        // Length score (up to 25 points)
        score += Math.min(password.length() * 2, 25);
        
        // Character variety (up to 75 points)
        if (UPPERCASE_PATTERN.matcher(password).find()) score += 15;
        if (LOWERCASE_PATTERN.matcher(password).find()) score += 15;
        if (DIGIT_PATTERN.matcher(password).find()) score += 15;
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) score += 15;
        
        // Length bonus for very long passwords
        if (password.length() >= 12) score += 10;
        if (password.length() >= 16) score += 5;

        return Math.min(score, 100);
    }

    public static class PasswordValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public PasswordValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
    }
    
    /**
     * Check password strength and provide detailed feedback
     */
    public PasswordStrengthResponse checkPasswordStrength(String password) {
        if (password == null) {
            return PasswordStrengthResponse.builder()
                    .isValid(false)
                    .strength("VERY_WEAK")
                    .score(0)
                    .errors(List.of("Password cannot be null"))
                    .suggestions(List.of("Please enter a password"))
                    .build();
        }
        
        PasswordValidationResult validation = validatePassword(password);
        int score = calculatePasswordScore(password);
        String strength = PasswordStrengthResponse.PasswordStrength.fromScore(score).getDisplayName();
        
        return PasswordStrengthResponse.builder()
                .isValid(validation.isValid())
                .strength(strength)
                .score(score)
                .errors(validation.getErrors())
                .suggestions(generatePasswordSuggestions(password))
                .hasUppercase(UPPERCASE_PATTERN.matcher(password).find())
                .hasLowercase(LOWERCASE_PATTERN.matcher(password).find())
                .hasDigits(DIGIT_PATTERN.matcher(password).find())
                .hasSpecialChars(SPECIAL_CHAR_PATTERN.matcher(password).find())
                .hasMinLength(password.length() >= minLength)
                .build();
    }
    
    private int calculatePasswordScore(String password) {
        int score = 0;
        
        // Length bonus
        score += Math.min(password.length() * 4, 40);
        
        // Character type bonuses
        if (UPPERCASE_PATTERN.matcher(password).find()) score += 15;
        if (LOWERCASE_PATTERN.matcher(password).find()) score += 15;
        if (DIGIT_PATTERN.matcher(password).find()) score += 15;
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) score += 15;
        
        // Complexity bonus for longer passwords
        if (password.length() > 12) score += 10;
        if (password.length() > 16) score += 10;
        
        return Math.max(0, Math.min(100, score));
    }
    
    private List<String> generatePasswordSuggestions(String password) {
        List<String> suggestions = new ArrayList<>();
        
        if (password.length() < minLength) {
            suggestions.add("Increase password length to at least " + minLength + " characters");
        }
        
        if (requireUppercase && !UPPERCASE_PATTERN.matcher(password).find()) {
            suggestions.add("Add uppercase letters (A-Z)");
        }
        
        if (requireLowercase && !LOWERCASE_PATTERN.matcher(password).find()) {
            suggestions.add("Add lowercase letters (a-z)");
        }
        
        if (requireDigits && !DIGIT_PATTERN.matcher(password).find()) {
            suggestions.add("Add numbers (0-9)");
        }
        
        if (requireSpecialChars && !SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            suggestions.add("Add special characters (!@#$%^&*)");
        }
        
        return suggestions;
    }
    
    /**
     * Check if password change is required
     */
    public boolean isPasswordChangeRequired(String email) {
        LocalDateTime lastChanged = passwordHistoryService.getLastPasswordChangeTime(email)
                .orElse(LocalDateTime.now().minusYears(1)); // If no history, assume very old password
                
        return lastChanged.plusDays(maxAgeDays)
                .isBefore(LocalDateTime.now());
    }
    
    /**
     * Check if password meets unique character requirement
     */
    public boolean hasEnoughUniqueCharacters(String password) {
        return password.chars().distinct().count() >= minUniqueChars;
    }
    
    /**
     * Check if password has been previously used
     */
    public boolean isPasswordPreviouslyUsed(String email, String password) {
        return passwordHistoryService.isPasswordPreviouslyUsed(email, password);
    }
}
