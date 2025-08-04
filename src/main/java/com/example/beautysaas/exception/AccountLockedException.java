package com.example.beautysaas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.LOCKED)
public class AccountLockedException extends RuntimeException {
    
    private final String email;
    private final int lockoutDurationMinutes;
    private final int attemptCount;
    
    public AccountLockedException(String email, int lockoutDurationMinutes, int attemptCount) {
        super(String.format("Account locked for user %s. Lockout duration: %d minutes. Failed attempts: %d", 
            email, lockoutDurationMinutes, attemptCount));
        this.email = email;
        this.lockoutDurationMinutes = lockoutDurationMinutes;
        this.attemptCount = attemptCount;
    }
    
    public AccountLockedException(String email, String message) {
        super(message);
        this.email = email;
        this.lockoutDurationMinutes = 0;
        this.attemptCount = 0;
    }
    
    public String getEmail() {
        return email;
    }
    
    public int getLockoutDurationMinutes() {
        return lockoutDurationMinutes;
    }
    
    public int getAttemptCount() {
        return attemptCount;
    }
}
