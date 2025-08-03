package com.example.beauty_saas.controller;

import com.example.beautysaas.dto.successfulstudent.SuccessfulStudentCreateRequest;
import com.example.beautysaas.dto.successfulstudent.SuccessfulStudentDto;
import com.example.beautysaas.dto.successfulstudent.SuccessfulStudentUpdateRequest;
import com.example.beautysaas.service.SuccessfulStudentService;
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
@RequestMapping("/api/admin/{parlourId}/successful-students")
@Tag(name = "Successful Student Management", description = "APIs for managing successful students by Parlour Admin")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class SuccessfulStudentController {

    private final SuccessfulStudentService successfulStudentService;

    public SuccessfulStudentController(SuccessfulStudentService successfulStudentService) {
        this.successfulStudentService = successfulStudentService;
    }

    @Operation(summary = "Create a new successful student entry", description = "Creates a new entry for a successful student who completed a course.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessfulStudentDto> createSuccessfulStudent(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Valid @RequestBody SuccessfulStudentCreateRequest createRequest) {
        log.info("Admin {} attempting to create successful student entry for parlour {}.", userDetails.getUsername(), parlourId);
        SuccessfulStudentDto student = successfulStudentService.createSuccessfulStudent(userDetails.getUsername(), parlourId, createRequest);
        log.info("Successful student entry created successfully with ID: {}", student.getId());
        return new ResponseEntity<>(student, HttpStatus.CREATED);
    }

    @Operation(summary = "Get successful student by ID", description = "Retrieves a successful student entry by its ID for a specific parlour.")
    @GetMapping("/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<SuccessfulStudentDto> getSuccessfulStudentById(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the successful student entry to retrieve", required = true) @PathVariable UUID studentId) {
        log.info("User {} fetching successful student {} for parlour {}.", userDetails.getUsername(), studentId, parlourId);
        SuccessfulStudentDto student = successfulStudentService.getSuccessfulStudentById(userDetails.getUsername(), parlourId, studentId);
        return ResponseEntity.ok(student);
    }

    @Operation(summary = "Get all successful students for a parlour", description = "Retrieves a paginated list of all successful student entries for a specific parlour.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Page<SuccessfulStudentDto>> getAllSuccessfulStudents(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort order (e.g., 'completionDate,desc')", example = "completionDate,desc") @RequestParam(defaultValue = "completionDate,desc") String[] sort) {
        log.info("User {} fetching all successful students for parlour {}.", userDetails.getUsername(), parlourId);
        Pageable pageable = PaginationUtil.createPageable(page, size, sort);
        Page<SuccessfulStudentDto> students = successfulStudentService.getAllSuccessfulStudents(userDetails.getUsername(), parlourId, pageable);
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "Update a successful student entry", description = "Updates an existing successful student entry within a specific parlour.")
    @PutMapping("/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessfulStudentDto> updateSuccessfulStudent(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the successful student entry to update", required = true) @PathVariable UUID studentId,
            @Valid @RequestBody SuccessfulStudentUpdateRequest updateRequest) {
        log.info("Admin {} attempting to update successful student {} for parlour {}.", userDetails.getUsername(), studentId, parlourId);
        SuccessfulStudentDto updatedStudent = successfulStudentService.updateSuccessfulStudent(userDetails.getUsername(), parlourId, studentId, updateRequest);
        log.info("Successful student {} updated successfully.", studentId);
        return ResponseEntity.ok(updatedStudent);
    }

    @Operation(summary = "Delete a successful student entry", description = "Deletes a successful student entry from a specific parlour.")
    @DeleteMapping("/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSuccessfulStudent(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the successful student entry to delete", required = true) @PathVariable UUID studentId) {
        log.info("Admin {} attempting to delete successful student {} from parlour {}.", userDetails.getUsername(), studentId, parlourId);
        successfulStudentService.deleteSuccessfulStudent(userDetails.getUsername(), parlourId, studentId);
        log.info("Successful student {} deleted successfully.", studentId);
        return ResponseEntity.ok("Successful student entry deleted successfully.");
    }
}
