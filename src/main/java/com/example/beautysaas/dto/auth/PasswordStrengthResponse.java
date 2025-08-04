package com.example.beautysaas.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordStrengthResponse {
    private int score; // 0-100
    private String strength; // WEAK, FAIR, GOOD, STRONG
    private String feedback;
    
    public static PasswordStrengthResponse create(int score) {
        String strength;
        String feedback;
        
        if (score < 30) {
            strength = "WEAK";
            feedback = "Password is too weak. Consider adding more characters, numbers, and special characters.";
        } else if (score < 60) {
            strength = "FAIR";
            feedback = "Password strength is fair. Consider making it longer or adding more variety.";
        } else if (score < 80) {
            strength = "GOOD";
            feedback = "Good password strength. You're on the right track!";
        } else {
            strength = "STRONG";
            feedback = "Excellent password strength!";
        }
        
        return new PasswordStrengthResponse(score, strength, feedback);
    }
}
