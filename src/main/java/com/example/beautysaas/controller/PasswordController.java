package com.example.beautysaas.controller;

import com.example.beautysaas.dto.auth.PasswordStrengthResponse;
import com.example.beautysaas.service.PasswordPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Password Management", description = "APIs for password validation and strength checking")
public class PasswordController {

    private final PasswordPolicyService passwordPolicyService;

    @Operation(summary = "Check Password Strength", description = "Evaluates password strength and provides feedback")
    @PostMapping("/check-strength")
    public ResponseEntity<PasswordStrengthResponse> checkPasswordStrength(@RequestBody String password) {
        log.debug("Checking password strength");
        
        int strengthScore = passwordPolicyService.calculatePasswordStrength(password);
        PasswordStrengthResponse response = PasswordStrengthResponse.create(strengthScore);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validate Password Policy", description = "Validates if password meets security policy requirements")
    @PostMapping("/validate")
    public ResponseEntity<PasswordPolicyService.PasswordValidationResult> validatePassword(@RequestBody String password) {
        log.debug("Validating password against policy");
        
        PasswordPolicyService.PasswordValidationResult result = passwordPolicyService.validatePassword(password);
        
        return ResponseEntity.ok(result);
    }
}
