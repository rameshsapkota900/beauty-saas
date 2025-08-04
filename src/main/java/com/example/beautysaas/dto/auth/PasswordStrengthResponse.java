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
}