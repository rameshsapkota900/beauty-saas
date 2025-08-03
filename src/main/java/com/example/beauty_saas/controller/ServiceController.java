package com.example.beauty_saas.controller;

import com.example.beautysaas.dto.service.ServiceCreateRequest;
import com.example.beautysaas.dto.service.ServiceDto;
import com.example.beautysaas.dto.service.ServiceUpdateRequest;
import com.example.beautysaas.service.ServiceService;
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
@RequestMapping("/api/admin/{parlourId}/services")
@Tag(name = "Service Management", description = "APIs for managing services by Parlour Admin")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @Operation(summary = "Create a new service", description = "Creates a new service within a specific parlour.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceDto> createService(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Valid @RequestBody ServiceCreateRequest createRequest) {
        log.info("Admin {} attempting to create service for parlour {}.", userDetails.getUsername(), parlourId);
        ServiceDto service = serviceService.createService(userDetails.getUsername(), parlourId, createRequest);
        log.info("Service created successfully with ID: {}", service.getId());
        return new ResponseEntity<>(service, HttpStatus.CREATED);
    }

    @Operation(summary = "Get service by ID", description = "Retrieves a service by its ID for a specific parlour.")
    @GetMapping("/{serviceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<ServiceDto> getServiceById(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the service to retrieve", required = true) @PathVariable UUID serviceId) {
        log.info("User {} fetching service {} for parlour {}.", userDetails.getUsername(), serviceId, parlourId);
        ServiceDto service = serviceService.getServiceById(userDetails.getUsername(), parlourId, serviceId);
        return ResponseEntity.ok(service);
    }

    @Operation(summary = "Get all services for a parlour", description = "Retrieves a paginated list of all services for a specific parlour.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Page<ServiceDto>> getAllServices(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort order (e.g., 'name,asc')", example = "name,asc") @RequestParam(defaultValue = "name,asc") String[] sort) {
        log.info("User {} fetching all services for parlour {}.", userDetails.getUsername(), parlourId);
        Pageable pageable = PaginationUtil.createPageable(page, size, sort);
        Page<ServiceDto> services = serviceService.getAllServices(userDetails.getUsername(), parlourId, pageable);
        return ResponseEntity.ok(services);
    }

    @Operation(summary = "Update a service", description = "Updates an existing service within a specific parlour.")
    @PutMapping("/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceDto> updateService(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the service to update", required = true) @PathVariable UUID serviceId,
            @Valid @RequestBody ServiceUpdateRequest updateRequest) {
        log.info("Admin {} attempting to update service {} for parlour {}.", userDetails.getUsername(), serviceId, parlourId);
        ServiceDto updatedService = serviceService.updateService(userDetails.getUsername(), parlourId, serviceId, updateRequest);
        log.info("Service {} updated successfully.", serviceId);
        return ResponseEntity.ok(updatedService);
    }

    @Operation(summary = "Delete a service", description = "Deletes a service from a specific parlour.")
    @DeleteMapping("/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteService(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the service to delete", required = true) @PathVariable UUID serviceId) {
        log.info("Admin {} attempting to delete service {} from parlour {}.", userDetails.getUsername(), serviceId, parlourId);
        serviceService.deleteService(userDetails.getUsername(), parlourId, serviceId);
        log.info("Service {} deleted successfully.", serviceId);
        return ResponseEntity.ok("Service deleted successfully.");
    }
}
