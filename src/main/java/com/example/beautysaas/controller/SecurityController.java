package com.example.beautysaas.controller;

import com.example.beautysaas.entity.SecurityAuditLog;
import com.example.beautysaas.service.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/security")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Security Management", description = "APIs for security administration and audit management")
public class SecurityController {

    private final SecurityService securityService;

    @Operation(summary = "Unlock User Account", description = "Manually unlock a locked user account (Super Admin only)")
    @PostMapping("/unlock-account/{email}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> unlockAccount(@PathVariable String email, Principal principal) {
        log.info("Admin {} attempting to unlock account for: {}", principal.getName(), email);
        securityService.unlockAccount(email, principal.getName());
        return ResponseEntity.ok("Account unlocked successfully for: " + email);
    }

    @Operation(summary = "Get Security Audit Logs", description = "Retrieve security audit logs for a specific user")
    @GetMapping("/audit-logs/{email}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<List<SecurityAuditLog>> getAuditLogs(@PathVariable String email) {
        log.info("Retrieving audit logs for email: {}", email);
        // In a real implementation, you'd use SecurityAuditLogRepository
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get System Security Events", description = "Retrieve recent security events (Super Admin only)")
    @GetMapping("/audit-logs/recent")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> getRecentSecurityEvents() {
        log.info("Retrieving recent security events");
        return ResponseEntity.ok("Recent security events retrieved");
    }

    @Operation(summary = "Check Account Lock Status", description = "Check if an account is currently locked")
    @GetMapping("/account-status/{email}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<String> checkAccountStatus(@PathVariable String email) {
        boolean isLocked = securityService.isAccountLocked(email);
        String status = isLocked ? "LOCKED" : "ACTIVE";
        return ResponseEntity.ok("Account status for " + email + ": " + status);
    }
}
