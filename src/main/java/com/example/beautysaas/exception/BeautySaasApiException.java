package com.example.beautysaas.exception;

import org.springframework.http.HttpStatus;

public class BeautySaasApiException extends RuntimeException {
    private HttpStatus status;
    private String message;

    public BeautySaasApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public BeautySaasApiException(String message, HttpStatus status, String message1) {
        super(message);
        this.status = status;
        this.message = message1;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
