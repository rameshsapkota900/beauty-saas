package com.example.beauty_saas.controller;

import com.example.beautysaas.dto.certificate.CertificateCreateRequest;
import com.example.beautysaas.dto.certificate.CertificateDto;
import com.example.beautysaas.dto.certificate.CertificateUpdateRequest;
import com.example.beautysaas.service.CertificateService;
import com.example.beautysaas.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/{parlourId}/certificates")
@Tag(name = "Certificate Management", description = "APIs for managing course certificates by Parlour Admin")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @Operation(summary = "Create a new certificate", description = "Creates a new certificate for a successful student.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CertificateDto> createCertificate(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Valid @RequestBody CertificateCreateRequest createRequest) {
        log.info("Admin {} attempting to create certificate for parlour {}.", userDetails.getUsername(), parlourId);
        CertificateDto certificate = certificateService.createCertificate(userDetails.getUsername(), parlourId, createRequest);
        log.info("Certificate created successfully with ID: {}", certificate.getId());
        return new ResponseEntity<>(certificate, HttpStatus.CREATED);
    }

    @Operation(summary = "Get certificate by ID", description = "Retrieves a certificate by its ID for a specific parlour.")
    @GetMapping("/{certificateId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<CertificateDto> getCertificateById(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the certificate to retrieve", required = true) @PathVariable UUID certificateId) {
        log.info("User {} fetching certificate {} for parlour {}.", userDetails.getUsername(), certificateId, parlourId);
        CertificateDto certificate = certificateService.getCertificateById(userDetails.getUsername(), parlourId, certificateId);
        return ResponseEntity.ok(certificate);
    }

    @Operation(summary = "Get all certificates for a parlour", description = "Retrieves a paginated list of all certificates for a specific parlour.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Page<CertificateDto>> getAllCertificates(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort order (e.g., 'issueDate,desc')", example = "issueDate,desc") @RequestParam(defaultValue = "issueDate,desc") String[] sort) {
        log.info("User {} fetching all certificates for parlour {}.", userDetails.getUsername(), parlourId);
        Pageable pageable = PaginationUtil.createPageable(page, size, sort);
        Page<CertificateDto> certificates = certificateService.getAllCertificates(userDetails.getUsername(), parlourId, pageable);
        return ResponseEntity.ok(certificates);
    }

    @Operation(summary = "Update a certificate", description = "Updates an existing certificate within a specific parlour.")
    @PutMapping("/{certificateId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CertificateDto> updateCertificate(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the certificate to update", required = true) @PathVariable UUID certificateId,
            @Valid @RequestBody CertificateUpdateRequest updateRequest) {
        log.info("Admin {} attempting to update certificate {} for parlour {}.", userDetails.getUsername(), certificateId, parlourId);
        CertificateDto updatedCertificate = certificateService.updateCertificate(userDetails.getUsername(), parlourId, certificateId, updateRequest);
        log.info("Certificate {} updated successfully.", certificateId);
        return ResponseEntity.ok(updatedCertificate);
    }

    @Operation(summary = "Delete a certificate", description = "Deletes a certificate from a specific parlour.")
    @DeleteMapping("/{certificateId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCertificate(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the certificate to delete", required = true) @PathVariable UUID certificateId) {
        log.info("Admin {} attempting to delete certificate {} from parlour {}.", userDetails.getUsername(), certificateId, parlourId);
        certificateService.deleteCertificate(userDetails.getUsername(), parlourId, certificateId);
        log.info("Certificate {} deleted successfully.", certificateId);
        return ResponseEntity.ok("Certificate deleted successfully.");
    }
}
