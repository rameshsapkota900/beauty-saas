package com.example.beautysaas.controller;

import com.example.beautysaas.dto.auth.PasswordChangeRequest;
import com.example.beautysaas.dto.auth.PasswordStrengthResponse;
import com.example.beautysaas.service.PasswordPolicyService;
import com.example.beautysaas.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/password")
@Tag(name = "Password Management", description = "APIs for password management and validation")
@Slf4j
public class PasswordController {

    private final PasswordPolicyService passwordPolicyService;
    private final UserService userService;

    public PasswordController(PasswordPolicyService passwordPolicyService, UserService userService) {
        this.passwordPolicyService = passwordPolicyService;
        this.userService = userService;
    }

    @Operation(summary = "Check Password Strength", description = "Validates password strength and provides feedback")
    @PostMapping("/check-strength")
    public ResponseEntity<PasswordStrengthResponse> checkPasswordStrength(@RequestParam String password) {
        log.debug("Checking password strength");
        PasswordStrengthResponse response = passwordPolicyService.checkPasswordStrength(password);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Change Password", description = "Allows authenticated users to change their password")
    @PostMapping("/change")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changePassword(
            Principal principal,
            @Valid @RequestBody PasswordChangeRequest request) {
        log.info("Password change request for user: {}", principal.getName());
        userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @Operation(summary = "Validate Password Policy", description = "Validates if password meets policy requirements")
    @PostMapping("/validate")
    public ResponseEntity<PasswordPolicyService.PasswordValidationResult> validatePassword(@RequestParam String password) {
        log.debug("Validating password against policy");
        PasswordPolicyService.PasswordValidationResult result = passwordPolicyService.validatePassword(password);
        return ResponseEntity.ok(result);
    }
}
