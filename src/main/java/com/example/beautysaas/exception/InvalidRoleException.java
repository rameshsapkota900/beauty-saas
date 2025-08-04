package com.example.beautysaas.exception;

import org.springframework.http.HttpStatus;

public class InvalidRoleException extends BeautySaasApiException {
    
    public InvalidRoleException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
    
    public InvalidRoleException(String roleName, String operation) {
        super(HttpStatus.FORBIDDEN, 
            String.format("Role '%s' is not authorized to perform operation: %s", roleName, operation));
    }
}
