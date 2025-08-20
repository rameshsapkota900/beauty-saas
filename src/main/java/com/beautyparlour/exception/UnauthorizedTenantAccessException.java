package com.beautyparlour.exception;

/**
 * Exception thrown when a user tries to access resources belonging to another tenant
 */
public class UnauthorizedTenantAccessException extends RuntimeException {
    
    public UnauthorizedTenantAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedTenantAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
