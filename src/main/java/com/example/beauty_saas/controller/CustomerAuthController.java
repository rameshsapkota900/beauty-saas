package com.example.beauty_saas.controller;

import com.example.beautysaas.dto.auth.JwtAuthResponse;
import com.example.beautysaas.dto.auth.LoginRequest;
import com.example.beautysaas.dto.auth.RegisterRequest;
import com.example.beautysaas.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/customer")
@Tag(name = "Customer Authentication", description = "APIs for Customer registration and login")
@Slf4j
public class CustomerAuthController {

    private final AuthService authService;

    public CustomerAuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new Customer", description = "Registers a new customer user.")
    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Attempting to register new customer: {}", registerRequest.getEmail());
        String response = authService.registerCustomer(registerRequest);
        log.info("Customer registration successful for: {}", registerRequest.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Customer Login", description = "Authenticates a customer user and returns a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> loginCustomer(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Attempting customer login for: {}", loginRequest.getEmail());
        JwtAuthResponse response = authService.loginCustomer(loginRequest);
        log.info("Customer login successful for: {}", loginRequest.getEmail());
        return ResponseEntity.ok(response);
    }
}
