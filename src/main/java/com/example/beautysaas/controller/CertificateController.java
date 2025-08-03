package com.example.beautysaas.controller;

import com.example.beautysaas.dto.certificate.CertificateCreateRequest;
import com.example.beautysaas.dto.certificate.CertificateDto;
import com.example.beautysaas.dto.certificate.CertificateUpdateRequest;
import com.example.beautysaas.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping
@Tag(name = "Certificate Management", description = "APIs for managing beauty parlour certificates")
@Slf4j
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @Operation(summary = "Add Certificate", description = "Allows an Admin to add a new certificate for their parlour.")
    @PostMapping("/admin/certificates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CertificateDto> addCertificate(
            Principal principal,
            @Parameter(description = "ID of the parlour to associate the certificate with", required = true) @RequestParam UUID parlourId,
            @Valid @RequestBody CertificateCreateRequest createRequest) {
        log.info("Admin {} adding certificate for parlour {}: {}", principal.getName(), parlourId, createRequest.getTitle());
        CertificateDto certificate = certificateService.addCertificate(principal.getName(), parlourId, createRequest);
        return new ResponseEntity<>(certificate, HttpStatus.CREATED);
    }

    @Operation(summary = "List Certificates (Public)", description = "Retrieves a paginated list of certificates for a specific parlour. Accessible publicly.")
    @GetMapping("/certificates")
    public ResponseEntity<Page<CertificateDto>> listCertificates(
            @Parameter(description = "ID of the parlour to retrieve certificates from", required = true) @RequestParam UUID parlourId,
            Pageable pageable) {
        log.debug("Listing certificates for parlour {}.", parlourId);
        Page<CertificateDto> certificates = certificateService.listCertificates(parlourId, pageable);
        return ResponseEntity.ok(certificates);
    }

    @Operation(summary = "Get Certificate Detail", description = "Retrieves a single certificate by its ID. Accessible publicly.")
    @GetMapping("/certificates/{id}")
    public ResponseEntity<CertificateDto> getCertificateDetail(@PathVariable UUID id) {
        log.debug("Fetching certificate by ID: {}", id);
        CertificateDto certificate = certificateService.getCertificateDetail(id);
        return ResponseEntity.ok(certificate);
    }

    @Operation(summary = "Update Certificate", description = "Allows an Admin to update an existing certificate for their parlour.")
    @PutMapping("/admin/certificates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CertificateDto> updateCertificate(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody CertificateUpdateRequest updateRequest) {
        log.info("Admin {} updating certificate {}.", principal.getName(), id);
        CertificateDto updatedCertificate = certificateService.updateCertificate(principal.getName(), id, updateRequest);
        return ResponseEntity.ok(updatedCertificate);
    }

    @Operation(summary = "Delete Certificate", description = "Allows an Admin to delete a certificate from their parlour.")
    @DeleteMapping("/admin/certificates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCertificate(Principal principal, @PathVariable UUID id) {
        log.info("Admin {} deleting certificate {}.", principal.getName(), id);
        certificateService.deleteCertificate(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
