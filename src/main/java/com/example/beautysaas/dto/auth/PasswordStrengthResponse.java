
package com.example.beautysaas.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for password strength feedback and validation results.
 * Used to provide detailed feedback to users about password quality.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordStrengthResponse {
    /**
     * Whether the password is valid according to policy.
     */
    private boolean isValid;

    /**
     * Password strength label (WEAK, MODERATE, STRONG, VERY_STRONG).
     */
    private String strength;

    /**
     * Password strength score (0-100).
     */
    private int score;

    /**
     * List of validation errors.
     */
    private List<String> errors;

    /**
     * List of suggestions to improve password strength.
     */
    private List<String> suggestions;

    /**
     * Whether the password contains uppercase letters.
     */
    private boolean hasUppercase;

    /**
     * Whether the password contains lowercase letters.
     */
    private boolean hasLowercase;

    /**
     * Whether the password contains digits.
     */
    private boolean hasDigits;

    /**
     * Whether the password contains special characters.
     */
    private boolean hasSpecialChars;

    /**
     * Whether the password meets the minimum length requirement.
     */
    private boolean hasMinLength;

    /**
     * Enum for password strength levels.
     */
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