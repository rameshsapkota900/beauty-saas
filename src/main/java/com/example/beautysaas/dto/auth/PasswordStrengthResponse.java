package com.example.beautysaas.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordStrengthResponse {
    private boolean isValid;
    private String strength; // WEAK, MODERATE, STRONG, VERY_STRONG
    private int score; // 0-100
    private List<String> errors;
    private List<String> suggestions;
    private boolean hasUppercase;
    private boolean hasLowercase;
    private boolean hasDigits;
    private boolean hasSpecialChars;
    private boolean hasMinLength;
    
    public enum PasswordStrength {
        VERY_WEAK(0, 20, "Very Weak"),
        WEAK(21, 40, "Weak"),
        MODERATE(41, 60, "Moderate"),
        STRONG(61, 80, "Strong"),
        VERY_STRONG(81, 100, "Very Strong");
        
        private final int minScore;
        private final int maxScore;
        private final String displayName;
        
        PasswordStrength(int minScore, int maxScore, String displayName) {
            this.minScore = minScore;
            this.maxScore = maxScore;
            this.displayName = displayName;
        }
        
        public static PasswordStrength fromScore(int score) {
            for (PasswordStrength strength : values()) {
                if (score >= strength.minScore && score <= strength.maxScore) {
                    return strength;
                }
            }
            return VERY_WEAK;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}