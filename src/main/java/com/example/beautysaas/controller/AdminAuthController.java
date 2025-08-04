package com.example.beautysaas.controller;

import com.example.beautysaas.dto.auth.JwtAuthResponse;
import com.example.beautysaas.dto.auth.LoginRequest;
import com.example.beautysaas.dto.user.UserProfileUpdateRequest;
import com.example.beautysaas.dto.user.UserDto;
import com.example.beautysaas.service.AuthService;
import com.example.beautysaas.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@RequestMapping
@Tag(name = "Admin Authentication & Profile", description = "APIs for Admin login and profile management")
@Slf4j
public class AdminAuthController {

    private final AuthService authService;
    private final UserService userService;

    public AdminAuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @Operation(summary = "Admin Secret Login", description = "Admin login with account lockout protection and audit logging.")
    @PostMapping("/admin-login/{parlourSlug}")
    public ResponseEntity<JwtAuthResponse> adminSecretLogin(@PathVariable String parlourSlug, @Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        log.info("Attempting admin secret login for parlour slug: {} with email: {}", parlourSlug, loginRequest.getEmail());
        JwtAuthResponse response = authService.adminLogin(parlourSlug, loginRequest, request);
        log.info("Admin secret login successful for parlour slug: {}", parlourSlug);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Admin Profile", description = "Retrieves the authenticated Admin's profile information.")
    @GetMapping("/admin/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getAdminProfile(Principal principal) {
        log.info("Fetching admin profile for user: {}", principal.getName());
        UserDto userDto = userService.getUserProfile(principal.getName());
        return ResponseEntity.ok(userDto);
        }

    @Operation(summary = "Update Admin Profile", description = "Updates the authenticated Admin's profile information.")
    @PatchMapping("/admin/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateAdminProfile(Principal principal, @Valid @RequestBody UserProfileUpdateRequest updateRequest) {
        log.info("Updating admin profile for user: {}", principal.getName());
        UserDto updatedUser = userService.updateUserProfile(principal.getName(), updateRequest);
        log.info("Admin profile updated for user: {}", principal.getName());
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Super Admin Login", description = "Authentication endpoint for Super Admin users with full security protection.")
    @PostMapping("/superadmin-login")
    public ResponseEntity<JwtAuthResponse> superAdminLogin(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        log.info("Attempting super admin login for email: {}", loginRequest.getEmail());
        JwtAuthResponse response = authService.superAdminLogin(loginRequest, request);
        log.info("Super admin login successful");
        return ResponseEntity.ok(response);
    }
}
