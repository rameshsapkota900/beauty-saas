package com.example.beautysaas.controller;

import com.example.beautysaas.dto.security.*;
import com.example.beautysaas.service.SecurityChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/security/challenges")
@RequiredArgsConstructor
public class SecurityChallengeController {

    private final SecurityChallengeService securityChallengeService;

    @PostMapping
    public ResponseEntity<SecurityChallengeResponse> createChallenge(
            @Valid @RequestBody SecurityChallengeRequest request) {
        return ResponseEntity.ok(securityChallengeService.createChallenge(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<SecurityChallengeResponse> verifyChallenge(
            @Valid @RequestBody SecurityChallengeVerifyRequest request) {
        return ResponseEntity.ok(securityChallengeService.verifyChallenge(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SecurityChallengeDTO> getChallenge(@PathVariable Long id) {
        return ResponseEntity.ok(securityChallengeService.getChallenge(id));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<SecurityChallengeDTO> getUserActiveChallenge(
            @PathVariable String email) {
        return ResponseEntity.ok(securityChallengeService.getUserActiveChallenge(email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> invalidateChallenge(@PathVariable Long id) {
        securityChallengeService.invalidateChallenge(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/risk/{email}")
    public ResponseEntity<SecurityRiskAssessmentDTO> getUserRiskAssessment(@PathVariable String email) {
        return ResponseEntity.ok(securityChallengeService.getUserRiskAssessment(email));
    }

    @PostMapping("/{id}/device-info")
    public ResponseEntity<SecurityChallengeResponse> updateDeviceInfo(
            @PathVariable Long id,
            @Valid @RequestBody DeviceInfoRequest request) {
        return ResponseEntity.ok(securityChallengeService.updateDeviceInfo(id, request));
    }

    @GetMapping("/security-level/{email}")
    public ResponseEntity<SecurityLevelDTO> getUserSecurityLevel(@PathVariable String email) {
        return ResponseEntity.ok(securityChallengeService.getUserSecurityLevel(email));
    }

    @PostMapping("/{id}/security-level")
    public ResponseEntity<SecurityChallengeResponse> updateSecurityLevel(
            @PathVariable Long id,
            @Valid @RequestBody SecurityLevelUpdateRequest request) {
        return ResponseEntity.ok(securityChallengeService.updateSecurityLevel(id, request));
    }

    @GetMapping("/stats/{email}")
    public ResponseEntity<SecurityStatsDTO> getUserSecurityStats(@PathVariable String email) {
        return ResponseEntity.ok(securityChallengeService.getUserSecurityStats(email));
    }

    @PostMapping("/{id}/reset-attempts")
    public ResponseEntity<SecurityChallengeResponse> resetAttempts(@PathVariable Long id) {
        return ResponseEntity.ok(securityChallengeService.resetAttempts(id));
    }
}
