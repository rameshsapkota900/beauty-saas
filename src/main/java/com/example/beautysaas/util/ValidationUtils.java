package com.example.beautysaas.util;

import com.example.beautysaas.exception.BeautySaasApiException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );

    private static final Pattern SLUG_PATTERN = Pattern.compile(
        "^[a-z0-9-]+$"
    );

    /**
     * Validate email format
     */
    public static void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Email cannot be empty");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }
    }

    /**
     * Validate phone number format
     */
    public static void validatePhoneNumber(String phoneNumber) {
        if (!StringUtils.hasText(phoneNumber)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Phone number cannot be empty");
        }
        
        String cleanPhone = phoneNumber.replaceAll("[\\s-()]", "");
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Invalid phone number format");
        }
    }

    /**
     * Validate parlour slug format
     */
    public static void validateSlug(String slug) {
        if (!StringUtils.hasText(slug)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Slug cannot be empty");
        }
        
        if (slug.length() < 3 || slug.length() > 50) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Slug must be between 3 and 50 characters");
        }
        
        if (!SLUG_PATTERN.matcher(slug).matches()) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, 
                "Slug can only contain lowercase letters, numbers, and hyphens");
        }
    }

    /**
     * Validate name field
     */
    public static void validateName(String name, String fieldName) {
        if (!StringUtils.hasText(name)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, fieldName + " cannot be empty");
        }
        
        if (name.length() < 2 || name.length() > 100) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, 
                fieldName + " must be between 2 and 100 characters");
        }
    }

    /**
     * Validate booking time is in the future
     */
    public static void validateFutureDateTime(LocalDateTime dateTime, String fieldName) {
        if (dateTime == null) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, fieldName + " cannot be null");
        }
        
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, 
                fieldName + " must be in the future");
        }
    }

    /**
     * Validate time range
     */
    public static void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Start time and end time cannot be null");
        }
        
        if (!startTime.isBefore(endTime)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Start time must be before end time");
        }
    }

    /**
     * Validate positive amount
     */
    public static void validatePositiveAmount(Double amount, String fieldName) {
        if (amount == null) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, fieldName + " cannot be null");
        }
        
        if (amount <= 0) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, fieldName + " must be positive");
        }
    }

    /**
     * Validate duration in minutes
     */
    public static void validateDuration(Integer minutes) {
        if (minutes == null) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Duration cannot be null");
        }
        
        if (minutes <= 0 || minutes > 480) { // Max 8 hours
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, 
                "Duration must be between 1 and 480 minutes");
        }
    }

    /**
     * Validate required string field
     */
    public static void validateRequired(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
    }

    /**
     * Generate slug from name
     */
    public static String generateSlug(String name) {
        if (!StringUtils.hasText(name)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Name cannot be empty for slug generation");
        }
        
        return name.toLowerCase()
                  .replaceAll("[^a-z0-9\\s-]", "") // Remove special characters except spaces and hyphens
                  .replaceAll("\\s+", "-") // Replace spaces with hyphens
                  .replaceAll("-+", "-") // Replace multiple hyphens with single hyphen
                  .replaceAll("^-|-$", ""); // Remove leading/trailing hyphens
    }
}
