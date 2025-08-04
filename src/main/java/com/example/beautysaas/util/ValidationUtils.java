package com.example.beautysaas.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[\\+]?[1-9]?[0-9]{7,15}$"
    );
    
    private static final Pattern SLUG_PATTERN = Pattern.compile(
        "^[a-z0-9]+(?:-[a-z0-9]+)*$"
    );
    
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z\\s.'\\-]{2,50}$"
    );
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isValidSlug(String slug) {
        return slug != null && SLUG_PATTERN.matcher(slug).matches();
    }
    
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }
    
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    public static boolean isWithinLength(String value, int minLength, int maxLength) {
        if (value == null) return false;
        int length = value.length();
        return length >= minLength && length <= maxLength;
    }
    
    public static boolean isValidPrice(String price) {
        if (price == null || price.trim().isEmpty()) return false;
        try {
            double value = Double.parseDouble(price);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isValidDuration(Integer duration) {
        return duration != null && duration > 0 && duration <= 1440; // Max 24 hours
    }
    
    public static String sanitizeInput(String input) {
        if (input == null) return null;
        return input.trim().replaceAll("<script[^>]*>.*?</script>", "")
                   .replaceAll("<[^>]+>", "");
    }
    
    public static String generateSlug(String input) {
        if (input == null) return null;
        return input.toLowerCase()
                   .replaceAll("[^a-z0-9\\s-]", "")
                   .replaceAll("\\s+", "-")
                   .replaceAll("-+", "-")
                   .replaceAll("^-|-$", "");
    }
}
