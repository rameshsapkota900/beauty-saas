package com.example.beauty_saas.controller;

import com.example.beautysaas.dto.parlour.ParlourCreateRequest;
import com.example.beautysaas.dto.parlour.ParlourDto;
import com.example.beautysaas.dto.parlour.ParlourUpdateRequest;
import com.example.beautysaas.service.ParlourService;
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
@RequestMapping("/api/superadmin/parlours")
@Tag(name = "SuperAdmin Parlour Management", description = "APIs for managing parlours by SuperAdmin")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class SuperAdminController {

    private final ParlourService parlourService;

    public SuperAdminController(ParlourService parlourService) {
        this.parlourService = parlourService;
    }

    @Operation(summary = "Create a new parlour", description = "Allows SuperAdmin to create a new parlour.")
    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ParlourDto> createParlour(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ParlourCreateRequest createRequest) {
        log.info("SuperAdmin {} attempting to create new parlour: {}", userDetails.getUsername(), createRequest.getName());
        ParlourDto parlour = parlourService.createParlour(createRequest);
        log.info("Parlour created successfully with ID: {}", parlour.getId());
        return new ResponseEntity<>(parlour, HttpStatus.CREATED);
    }

    @Operation(summary = "Get parlour by ID", description = "Retrieves a parlour by its ID.")
    @GetMapping("/{parlourId}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ParlourDto> getParlourById(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour to retrieve", required = true) @PathVariable UUID parlourId) {
        log.info("SuperAdmin {} fetching parlour with ID: {}", userDetails.getUsername(), parlourId);
        ParlourDto parlour = parlourService.getParlourById(parlourId);
        return ResponseEntity.ok(parlour);
    }

    @Operation(summary = "Get all parlours", description = "Retrieves a paginated list of all parlours.")
    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Page<ParlourDto>> getAllParlours(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort order (e.g., 'name,asc')", example = "name,asc") @RequestParam(defaultValue = "name,asc") String[] sort) {
        log.info("SuperAdmin {} fetching all parlours.", userDetails.getUsername());
        Pageable pageable = PaginationUtil.createPageable(page, size, sort);
        Page<ParlourDto> parlours = parlourService.getAllParlours(pageable);
        return ResponseEntity.ok(parlours);
    }

    @Operation(summary = "Update a parlour", description = "Updates an existing parlour.")
    @PutMapping("/{parlourId}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ParlourDto> updateParlour(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour to update", required = true) @PathVariable UUID parlourId,
            @Valid @RequestBody ParlourUpdateRequest updateRequest) {
        log.info("SuperAdmin {} attempting to update parlour with ID: {}", userDetails.getUsername(), parlourId);
        ParlourDto updatedParlour = parlourService.updateParlour(parlourId, updateRequest);
        log.info("Parlour {} updated successfully.", parlourId);
        return ResponseEntity.ok(updatedParlour);
    }

    @Operation(summary = "Delete a parlour", description = "Deletes a parlour and all associated data (Admin, Staff, Services, Courses, Bookings, etc.).")
    @DeleteMapping("/{parlourId}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<String> deleteParlour(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour to delete", required = true) @PathVariable UUID parlourId) {
        log.info("SuperAdmin {} attempting to delete parlour with ID: {}. This will cascade delete all associated data.", userDetails.getUsername(), parlourId);
        parlourService.deleteParlour(parlourId);
        log.info("Parlour {} and all associated data deleted successfully.", parlourId);
        return ResponseEntity.ok("Parlour and all associated data deleted successfully.");
    }
}
