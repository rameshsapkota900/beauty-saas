package com.beautyparlour.util;

import java.util.regex.Pattern;

/**
 * Utility class for phone number validation.
 */
public class PhoneValidationUtils {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    private PhoneValidationUtils() {}

    /**
     * Validates if the given phone number is a valid 10-digit number.
     * @param phone the phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
}
