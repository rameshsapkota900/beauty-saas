package com.example.beautysaas.exception;

import org.springframework.http.HttpStatus;

public class AccountLockedException extends BeautySaasApiException {
    
    public AccountLockedException(String email) {
        super(HttpStatus.LOCKED, 
            String.format("Account '%s' is temporarily locked due to multiple failed login attempts", email));
    }
    
    public AccountLockedException(String email, String lockoutUntil) {
        super(HttpStatus.LOCKED, 
            String.format("Account '%s' is locked until %s", email, lockoutUntil));
    }
}
