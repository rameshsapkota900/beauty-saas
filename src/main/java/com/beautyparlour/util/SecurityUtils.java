package com.beautyparlour.util;

import org.springframework.web.util.HtmlUtils;

import java.util.regex.Pattern;

/**
 * Utility class for input sanitization and validation
 */
public final class SecurityUtils {
    
    // XSS prevention patterns
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern SRC_PATTERN = Pattern.compile("src[\\r\\n]*=[\\r\\n]*\\'(.*?)\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern HREF_PATTERN = Pattern.compile("href[\\r\\n]*=[\\r\\n]*\\'(.*?)\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern ONLOAD_PATTERN = Pattern.compile("onload[^>]*=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern ONERROR_PATTERN = Pattern.compile("onerror[^>]*=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern EVAL_PATTERN = Pattern.compile("eval\\((.*)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("expression\\((.*)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
    private static final Pattern VBSCRIPT_PATTERN = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
    
    private SecurityUtils() {
        // Utility class
    }
    
    /**
     * Sanitize input to prevent XSS attacks
     * @param input The input string to sanitize
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        String sanitized = input;
        
        // HTML encode to prevent XSS
        sanitized = HtmlUtils.htmlEscape(sanitized);
        
        // Remove potentially dangerous patterns
        sanitized = SCRIPT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = SRC_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = HREF_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = ONLOAD_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = ONERROR_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = EVAL_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = EXPRESSION_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = JAVASCRIPT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = VBSCRIPT_PATTERN.matcher(sanitized).replaceAll("");
        
        return sanitized.trim();
    }
    
    /**
     * Validate and sanitize phone number
     * @param phone Phone number to validate
     * @return Clean phone number or null if invalid
     */
    public static String sanitizePhoneNumber(String phone) {
        if (phone == null) {
            return null;
        }
        
        // Remove all non-digit characters
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        
        // Validate length (assuming 10-digit phone numbers)
        if (cleanPhone.length() == 10) {
            return cleanPhone;
        }
        
        return null;
    }
    
    /**
     * Sanitize and validate URL
     * @param url URL to sanitize
     * @return Clean URL or null if invalid
     */
    public static String sanitizeUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return url;
        }
        
        String cleanUrl = url.trim().toLowerCase();
        
        // Only allow http and https protocols
        if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
            return null;
        }
        
        // Remove potential XSS in URL
        cleanUrl = sanitizeInput(cleanUrl);
        
        return cleanUrl;
    }
    
    /**
     * Validate string length and content
     * @param input Input string
     * @param minLength Minimum length
     * @param maxLength Maximum length
     * @return true if valid
     */
    public static boolean isValidLength(String input, int minLength, int maxLength) {
        if (input == null) {
            return minLength == 0;
        }
        
        int length = input.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Check if string contains only safe characters for names
     * @param name Name to validate
     * @return true if safe
     */
    public static boolean isSafeName(String name) {
        if (name == null) {
            return false;
        }
        
        // Allow letters, numbers, spaces, dots, hyphens, and common name characters
        Pattern safeNamePattern = Pattern.compile("^[a-zA-Z0-9\\s.-']+$");
        return safeNamePattern.matcher(name.trim()).matches();
    }
}
