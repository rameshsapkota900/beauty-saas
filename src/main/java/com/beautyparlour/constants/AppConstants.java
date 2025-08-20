package com.beautyparlour.constants;

/**
 * Application-wide constants for Beauty Parlour SaaS
 */
public final class AppConstants {
    
    // API Response Messages
    public static final class Messages {
        public static final String SUCCESS = "Operation successful";
        public static final String CREATED = "Resource created successfully";
        public static final String UPDATED = "Resource updated successfully";
        public static final String DELETED = "Resource deleted successfully";
        public static final String NOT_FOUND = "Resource not found";
        public static final String UNAUTHORIZED = "Unauthorized access";
        public static final String FORBIDDEN = "Access denied";
        public static final String VALIDATION_FAILED = "Validation failed";
        
        // Booking specific messages
        public static final String BOOKING_CREATED = "Booking created successfully";
        public static final String BOOKING_CANCELLED = "Booking cancelled successfully";
        public static final String BOOKING_STATUS_UPDATED = "Booking status updated successfully";
        
        // Authentication messages
        public static final String LOGIN_SUCCESS = "Login successful";
        public static final String INVALID_CREDENTIALS = "Invalid credentials";
        public static final String TOKEN_EXPIRED = "Token has expired";
        
        private Messages() {}
    }
    
    // Validation Constants
    public static final class Validation {
        public static final int MIN_NAME_LENGTH = 2;
        public static final int MAX_NAME_LENGTH = 100;
        public static final int MAX_DESCRIPTION_LENGTH = 1000;
        public static final int PHONE_LENGTH = 10;
        public static final String PHONE_PATTERN = "^[0-9]{10}$";
        public static final String URL_PATTERN = "^(https?://)?[\\w.-]+\\.[a-z]{2,}(/.*)?$|^$";
        public static final String NAME_PATTERN = "^[a-zA-Z0-9\\s.-]{2,100}$";
        
        private Validation() {}
    }
    
    // Pagination Constants
    public static final class Pagination {
        public static final int DEFAULT_PAGE_SIZE = 20;
        public static final int MAX_PAGE_SIZE = 100;
        public static final String DEFAULT_SORT_BY = "createdAt";
        public static final String DEFAULT_SORT_DIRECTION = "DESC";
        
        private Pagination() {}
    }
    
    // Security Constants
    public static final class Security {
        public static final String ROLE_ADMIN = "ADMIN";
        public static final String ROLE_SUPERADMIN = "SUPERADMIN";
        public static final String AUTHORIZATION_HEADER = "Authorization";
        public static final String BEARER_PREFIX = "Bearer ";
        public static final long JWT_EXPIRATION_MS = 86400000L; // 24 hours
        
        private Security() {}
    }
    
    // Business Constants
    public static final class Business {
        public static final int MAX_ADVANCE_PERCENTAGE = 80; // Maximum 80% of salary as advance
        public static final int MIN_SERVICE_PRICE = 1;
        public static final int MAX_SERVICE_PRICE = 999999;
        
        private Business() {}
    }
    
    private AppConstants() {
        // Utility class - prevent instantiation
    }
}
