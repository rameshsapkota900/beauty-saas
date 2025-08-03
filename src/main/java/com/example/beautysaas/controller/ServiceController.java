package com.example.beautysaas.controller;

import com.example.beautysaas.dto.service.ServiceCreateRequest;
import com.example.beautysaas.dto.service.ServiceDto;
import com.example.beautysaas.dto.service.ServiceUpdateRequest;
import com.example.beautysaas.service.ServiceService;
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
@Tag(name = "Service Management", description = "APIs for managing beauty parlour services")
@Slf4j
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @Operation(summary = "Add Service", description = "Allows an Admin to add a new service for their parlour.")
    @PostMapping("/admin/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceDto> addService(
            Principal principal,
            @Parameter(description = "ID of the parlour to associate the service with", required = true) @RequestParam UUID parlourId,
            @Valid @RequestBody ServiceCreateRequest createRequest) {
        log.info("Admin {} adding service for parlour {}: {}", principal.getName(), parlourId, createRequest.getName());
        ServiceDto service = serviceService.addService(principal.getName(), parlourId, createRequest);
        return new ResponseEntity<>(service, HttpStatus.CREATED);
    }

    @Operation(summary = "List Services (Public)", description = "Retrieves a paginated list of services for a specific parlour. Accessible publicly.")
    @GetMapping("/services")
    public ResponseEntity<Page<ServiceDto>> listServices(
            @Parameter(description = "ID of the parlour to retrieve services from", required = true) @RequestParam UUID parlourId,
            Pageable pageable) {
        log.debug("Listing services for parlour {}.", parlourId);
        Page<ServiceDto> services = serviceService.listServices(parlourId, pageable);
        return ResponseEntity.ok(services);
    }

    @Operation(summary = "Get Service Detail", description = "Retrieves a single service by its ID. Accessible publicly.")
    @GetMapping("/services/\{id}")
    public ResponseEntity<ServiceDto> getServiceDetail(@PathVariable UUID id) {
        log.debug("Fetching service by ID: {}", id);
        ServiceDto service = serviceService.getServiceDetail(id);
        return ResponseEntity.ok(service);
    }

    @Operation(summary = "Update Service", description = "Allows an Admin to update an existing service for their parlour.")
    @PutMapping("/admin/services/\{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceDto> updateService(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody ServiceUpdateRequest updateRequest) {
        log.info("Admin {} updating service {}.", principal.getName(), id);
        ServiceDto updatedService = serviceService.updateService(principal.getName(), id, updateRequest);
        return ResponseEntity.ok(updatedService);
    }

    @Operation(summary = "Delete Service", description = "Allows an Admin to delete a service from their parlour.")
    @DeleteMapping("/admin/services/\{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteService(Principal principal, @PathVariable UUID id) {
        log.info("Admin {} deleting service {}.", principal.getName(), id);
        serviceService.deleteService(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
