package com.example.beautysaas.controller;

import com.example.beautysaas.dto.auth.JwtAuthResponse;
import com.example.beautysaas.dto.auth.LoginRequest;
import com.example.beautysaas.dto.auth.RegisterRequest;
import com.example.beautysaas.dto.user.UserProfileUpdateRequest;
import com.example.beautysaas.dto.user.UserDto;
import com.example.beautysaas.service.AuthService;
import com.example.beautysaas.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@Tag(name = "Customer Authentication & Profile", description = "APIs for Customer registration, login and profile management")
@Slf4j
public class CustomerAuthController {

    private final AuthService authService;
    private final UserService userService;

    public CustomerAuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @Operation(summary = "Customer Registration", description = "Registers a new customer account.")
    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Attempting customer registration for email: {}", registerRequest.getEmail());
        authService.registerCustomer(registerRequest);
        log.info("Customer registered successfully: {}", registerRequest.getEmail());
        return new ResponseEntity<>("Customer registered successfully!", HttpStatus.CREATED);
    }

    @Operation(summary = "Customer Login", description = "Authenticates a customer and returns a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> customerLogin(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Attempting customer login for email: {}", loginRequest.getEmail());
        JwtAuthResponse response = authService.customerLogin(loginRequest);
        log.info("Customer login successful for email: {}", loginRequest.getEmail());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Customer Profile", description = "Retrieves the authenticated Customer's profile information.")
    @GetMapping("/customer/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<UserDto> getCustomerProfile(Principal principal) {
        log.info("Fetching customer profile for user: {}", principal.getName());
        UserDto userDto = userService.getUserProfile(principal.getName());
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Update Customer Profile", description = "Updates the authenticated Customer's profile information.")
    @PatchMapping("/customer/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<UserDto> updateCustomerProfile(Principal principal, @Valid @RequestBody UserProfileUpdateRequest updateRequest) {
        log.info("Updating customer profile for user: {}", principal.getName());
        UserDto updatedUser = userService.updateUserProfile(principal.getName(), updateRequest);
        log.info("Customer profile updated for user: {}", principal.getName());
        return ResponseEntity.ok(updatedUser);
    }
}
