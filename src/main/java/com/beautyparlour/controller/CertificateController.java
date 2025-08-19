package com.beautyparlour.controller;

import com.beautyparlour.dto.request.CreateCertificateRequest;
import com.beautyparlour.dto.response.ApiResponse;
import com.beautyparlour.entity.Certificate;
import com.beautyparlour.security.UserPrincipal;
import com.beautyparlour.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/certificates")
@Tag(name = "Certificates", description = "Certificate management APIs")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @PostMapping
    @Operation(summary = "Add certificate")
    public ResponseEntity<ApiResponse<Certificate>> createCertificate(
            @Valid @RequestBody CreateCertificateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Certificate certificate = certificateService.createCertificate(request, currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Certificate added successfully", certificate));
    }

    @GetMapping
    @Operation(summary = "Get certificates")
    public ResponseEntity<ApiResponse<List<Certificate>>> getCertificates(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<Certificate> certificates;
        if (currentUser != null && currentUser.getParlourId() != null) {
            certificates = certificateService.getCertificatesByParlour(currentUser.getParlourId());
        } else {
            certificates = certificateService.getAllCertificates();
        }
        return ResponseEntity.ok(ApiResponse.success("Certificates retrieved successfully", certificates));
    }

    @DeleteMapping("/{certificateId}")
    @Operation(summary = "Delete certificate")
    public ResponseEntity<ApiResponse<Void>> deleteCertificate(
            @PathVariable UUID certificateId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        certificateService.deleteCertificate(certificateId, currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Certificate deleted successfully"));
    }
}
