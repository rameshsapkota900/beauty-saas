package com.example.beauty_saas.controller;

import com.example.beautysaas.dto.advancepayment.AdvancePaymentCreateRequest;
import com.example.beautysaas.dto.advancepayment.AdvancePaymentDto;
import com.example.beautysaas.dto.salarylog.SalaryLogDto;
import com.example.beautysaas.dto.staff.StaffCreateRequest;
import com.example.beautysaas.dto.staff.StaffDto;
import com.example.beautysaas.dto.staff.StaffUpdateRequest;
import com.example.beautysaas.service.StaffService;
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
@RequestMapping("/api/admin/{parlourId}/staff")
@Tag(name = "Staff Management", description = "APIs for managing staff, advance payments, and salary logs by Parlour Admin")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @Operation(summary = "Create a new staff member", description = "Creates a new staff member for a specific parlour.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StaffDto> createStaff(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Valid @RequestBody StaffCreateRequest createRequest) {
        log.info("Admin {} attempting to create staff for parlour {}.", userDetails.getUsername(), parlourId);
        StaffDto staff = staffService.createStaff(userDetails.getUsername(), parlourId, createRequest);
        log.info("Staff created successfully with ID: {}", staff.getId());
        return new ResponseEntity<>(staff, HttpStatus.CREATED);
    }

    @Operation(summary = "Get staff member by ID", description = "Retrieves a staff member by their ID for a specific parlour.")
    @GetMapping("/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<StaffDto> getStaffById(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the staff member to retrieve", required = true) @PathVariable UUID staffId) {
        log.info("User {} fetching staff {} for parlour {}.", userDetails.getUsername(), staffId, parlourId);
        StaffDto staff = staffService.getStaffById(userDetails.getUsername(), parlourId, staffId);
        return ResponseEntity.ok(staff);
    }

    @Operation(summary = "Get all staff members for a parlour", description = "Retrieves a paginated list of all staff members for a specific parlour.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Page<StaffDto>> getAllStaff(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort order (e.g., 'name,asc')", example = "name,asc") @RequestParam(defaultValue = "name,asc") String[] sort) {
        log.info("User {} fetching all staff for parlour {}.", userDetails.getUsername(), parlourId);
        Pageable pageable = PaginationUtil.createPageable(page, size, sort);
        Page<StaffDto> staff = staffService.getAllStaff(userDetails.getUsername(), parlourId, pageable);
        return ResponseEntity.ok(staff);
    }

    @Operation(summary = "Update a staff member", description = "Updates an existing staff member within a specific parlour.")
    @PutMapping("/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StaffDto> updateStaff(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the staff member to update", required = true) @PathVariable UUID staffId,
            @Valid @RequestBody StaffUpdateRequest updateRequest) {
        log.info("Admin {} attempting to update staff {} for parlour {}.", userDetails.getUsername(), staffId, parlourId);
        StaffDto updatedStaff = staffService.updateStaff(userDetails.getUsername(), parlourId, staffId, updateRequest);
        log.info("Staff {} updated successfully.", staffId);
        return ResponseEntity.ok(updatedStaff);
    }

    @Operation(summary = "Delete a staff member", description = "Deletes a staff member from a specific parlour.")
    @DeleteMapping("/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteStaff(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the staff member to delete", required = true) @PathVariable UUID staffId) {
        log.info("Admin {} attempting to delete staff {} from parlour {}.", userDetails.getUsername(), staffId, parlourId);
        staffService.deleteStaff(userDetails.getUsername(), parlourId, staffId);
        log.info("Staff {} deleted successfully.", staffId);
        return ResponseEntity.ok("Staff deleted successfully.");
    }

    @Operation(summary = "Record an advance payment for staff", description = "Records an advance payment made to a staff member.")
    @PostMapping("/{staffId}/advance-payments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdvancePaymentDto> recordAdvancePayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the staff member", required = true) @PathVariable UUID staffId,
            @Valid @RequestBody AdvancePaymentCreateRequest createRequest) {
        log.info("Admin {} attempting to record advance payment for staff {} in parlour {}.", userDetails.getUsername(), staffId, parlourId);
        AdvancePaymentDto advancePayment = staffService.recordAdvancePayment(userDetails.getUsername(), parlourId, staffId, createRequest);
        log.info("Advance payment recorded successfully for staff {}.", staffId);
        return new ResponseEntity<>(advancePayment, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all advance payments for staff", description = "Retrieves a paginated list of all advance payments for a specific staff member.")
    @GetMapping("/{staffId}/advance-payments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AdvancePaymentDto>> getStaffAdvancePayments(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the staff member", required = true) @PathVariable UUID staffId,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort order (e.g., 'paymentDate,desc')", example = "paymentDate,desc") @RequestParam(defaultValue = "paymentDate,desc") String[] sort) {
        log.info("Admin {} fetching advance payments for staff {} in parlour {}.", userDetails.getUsername(), staffId, parlourId);
        Pageable pageable = PaginationUtil.createPageable(page, size, sort);
        Page<AdvancePaymentDto> advancePayments = staffService.getStaffAdvancePayments(userDetails.getUsername(), parlourId, staffId, pageable);
        return ResponseEntity.ok(advancePayments);
    }

    @Operation(summary = "Get all salary logs for staff", description = "Retrieves a paginated list of all salary logs for a specific staff member.")
    @GetMapping("/{staffId}/salary-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SalaryLogDto>> getStaffSalaryLogs(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the staff member", required = true) @PathVariable UUID staffId,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort order (e.g., 'paymentDate,desc')", example = "paymentDate,desc") @RequestParam(defaultValue = "paymentDate,desc") String[] sort) {
        log.info("Admin {} fetching salary logs for staff {} in parlour {}.", userDetails.getUsername(), staffId, parlourId);
        Pageable pageable = PaginationUtil.createPageable(page, size, sort);
        Page<SalaryLogDto> salaryLogs = staffService.getStaffSalaryLogs(userDetails.getUsername(), parlourId, staffId, pageable);
        return ResponseEntity.ok(salaryLogs);
    }
}
