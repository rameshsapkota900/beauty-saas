package com.example.beauty_saas.exception;

import org.springframework.http.HttpStatus;

public class BeautySaasApiException extends RuntimeException {
    private HttpStatus status;
    private String message;

    public BeautySaasApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
