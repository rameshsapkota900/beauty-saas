package com.example.beautysaas.controller;

import com.example.beautysaas.dto.parlour.ParlourCreateRequest;
import com.example.beautysaas.dto.parlour.ParlourDto;
import com.example.beautysaas.dto.parlour.ParlourUpdateRequest;
import com.example.beautysaas.service.ParlourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/superadmin/parlours")
@Tag(name = "SuperAdmin Parlour Management", description = "APIs for SuperAdmin to manage parlours and their associated admins")
@Slf4j
public class SuperAdminController {

    private final ParlourService parlourService;

    public SuperAdminController(ParlourService parlourService) {
        this.parlourService = parlourService;
    }

    @Operation(summary = "Create a new Parlour", description = "Allows SuperAdmin to create a new parlour along with its initial Admin user.")
    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ParlourDto> createParlour(@Valid @RequestBody ParlourCreateRequest createRequest) {
        log.info("SuperAdmin creating new parlour: {}", createRequest.getName());
        ParlourDto parlour = parlourService.createParlour(createRequest);
        return new ResponseEntity<>(parlour, HttpStatus.CREATED);
    }

    @Operation(summary = "List all Parlours", description = "Retrieves a paginated list of all parlours. Accessible by SuperAdmin.")
    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Page<ParlourDto>> listAllParlours(Pageable pageable) {
        log.debug("SuperAdmin listing all parlours.");
        Page<ParlourDto> parlours = parlourService.listAllParlours(pageable);
        return ResponseEntity.ok(parlours);
    }

    @Operation(summary = "Update Parlour information", description = "Allows SuperAdmin to update an existing parlour's information.")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ParlourDto> updateParlour(@PathVariable UUID id, @Valid @RequestBody ParlourUpdateRequest updateRequest) {
        log.info("SuperAdmin updating parlour {}.", id);
        ParlourDto updatedParlour = parlourService.updateParlour(id, updateRequest);
        return ResponseEntity.ok(updatedParlour);
    }

    @Operation(summary = "Delete Parlour", description = "Allows SuperAdmin to delete a parlour and its associated Admin user.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> deleteParlour(@PathVariable UUID id) {
        log.info("SuperAdmin deleting parlour {}.", id);
        parlourService.deleteParlour(id);
        return ResponseEntity.noContent().build();
    }
}
