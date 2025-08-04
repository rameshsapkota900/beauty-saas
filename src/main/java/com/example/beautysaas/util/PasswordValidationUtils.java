package com.example.beautysaas.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class PasswordValidationUtils {
    
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");
    private static final Pattern COMMON_SEQUENCES = Pattern.compile("(012|123|234|345|456|567|678|789|890|abc|bcd|cde|def|efg|fgh|ghi|hij|ijk|jkl|klm|lmn|mno|nop|opq|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz)");
    
    public static boolean hasUppercase(String password) {
        return UPPERCASE_PATTERN.matcher(password).find();
    }
    
    public static boolean hasLowercase(String password) {
        return LOWERCASE_PATTERN.matcher(password).find();
    }
    
    public static boolean hasDigit(String password) {
        return DIGIT_PATTERN.matcher(password).find();
    }
    
    public static boolean hasSpecialChar(String password) {
        return SPECIAL_CHAR_PATTERN.matcher(password).find();
    }
    
    public static boolean hasMinLength(String password, int minLength) {
        return password != null && password.length() >= minLength;
    }
    
    public static boolean hasRepeatingChars(String password) {
        if (password == null || password.length() < 3) return false;
        
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) && 
                password.charAt(i + 1) == password.charAt(i + 2)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean hasSequentialChars(String password) {
        if (password == null) return false;
        return COMMON_SEQUENCES.matcher(password.toLowerCase()).find();
    }
    
    public static int calculatePasswordScore(String password) {
        if (password == null || password.isEmpty()) return 0;
        
        int score = 0;
        
        // Base score for length
        score += Math.min(password.length() * 4, 40);
        
        // Bonus for character types
        if (hasUppercase(password)) score += 15;
        if (hasLowercase(password)) score += 15;
        if (hasDigit(password)) score += 15;
        if (hasSpecialChar(password)) score += 15;
        
        // Penalties
        if (hasRepeatingChars(password)) score -= 20;
        if (hasSequentialChars(password)) score -= 15;
        
        return Math.max(0, Math.min(100, score));
    }
}
