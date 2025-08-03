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
@RequestMapping("/api/auth/admin")
@Tag(name = "Admin Authentication", description = "APIs for Admin registration and login")
@Slf4j
public class AdminAuthController {

    private final AuthService authService;

    public AdminAuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new Admin", description = "Registers a new admin user for a specific parlour.")
    @PostMapping("/register")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Attempting to register new admin: {}", registerRequest.getEmail());
        String response = authService.registerAdmin(registerRequest);
        log.info("Admin registration successful for: {}", registerRequest.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Admin Login", description = "Authenticates an admin user and returns a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> loginAdmin(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Attempting admin login for: {}", loginRequest.getEmail());
        JwtAuthResponse response = authService.loginAdmin(loginRequest);
        log.info("Admin login successful for: {}", loginRequest.getEmail());
        return ResponseEntity.ok(response);
    }
}
