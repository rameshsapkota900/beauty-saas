package com.example.beautysaas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class InvalidRoleException extends RuntimeException {
    
    private final String userEmail;
    private final String currentRole;
    private final String requiredRole;
    
    public InvalidRoleException(String userEmail, String currentRole, String requiredRole) {
        super(String.format("User %s with role %s does not have required role %s", 
            userEmail, currentRole, requiredRole));
        this.userEmail = userEmail;
        this.currentRole = currentRole;
        this.requiredRole = requiredRole;
    }
    
    public InvalidRoleException(String message) {
        super(message);
        this.userEmail = null;
        this.currentRole = null;
        this.requiredRole = null;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public String getCurrentRole() {
        return currentRole;
    }
    
    public String getRequiredRole() {
        return requiredRole;
    }
}
