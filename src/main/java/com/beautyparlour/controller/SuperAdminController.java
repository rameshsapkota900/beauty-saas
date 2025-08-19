package com.beautyparlour.controller;

import com.beautyparlour.dto.request.CreateParlourRequest;
import com.beautyparlour.dto.request.SuperAdminLoginRequest;
import com.beautyparlour.dto.response.ApiResponse;
import com.beautyparlour.dto.response.LoginResponse;
import com.beautyparlour.dto.response.ParlourResponse;
import com.beautyparlour.service.AuthService;
import com.beautyparlour.service.SuperAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/superadmin")
@Tag(name = "SuperAdmin", description = "SuperAdmin management APIs")
public class SuperAdminController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SuperAdminService superAdminService;

    @PostMapping("/secret-login")
    @Operation(summary = "SuperAdmin login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody SuperAdminLoginRequest request) {
        LoginResponse response = authService.authenticateSuperAdmin(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/parlours")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Create new parlour")
    public ResponseEntity<ApiResponse<ParlourResponse>> createParlour(@Valid @RequestBody CreateParlourRequest request) {
        ParlourResponse response = superAdminService.createParlour(request);
        return ResponseEntity.ok(ApiResponse.success("Parlour created successfully", response));
    }

    @GetMapping("/parlours")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Get all parlours")
    public ResponseEntity<ApiResponse<List<ParlourResponse>>> getAllParlours() {
        List<ParlourResponse> parlours = superAdminService.getAllParlours();
        return ResponseEntity.ok(ApiResponse.success("Parlours retrieved successfully", parlours));
    }

    @DeleteMapping("/parlours/{parlourId}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Delete parlour")
    public ResponseEntity<ApiResponse<Void>> deleteParlour(@PathVariable UUID parlourId) {
        superAdminService.deleteParlour(parlourId);
        return ResponseEntity.ok(ApiResponse.success("Parlour deleted successfully"));
    }
}
