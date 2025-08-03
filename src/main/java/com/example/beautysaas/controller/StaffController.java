package com.example.beautysaas.controller;

import com.example.beautysaas.dto.advancepayment.AdvancePaymentCreateRequest;
import com.example.beautysaas.dto.advancepayment.AdvancePaymentDto;
import com.example.beautysaas.dto.salarylog.SalaryLogDto;
import com.example.beautysaas.dto.staff.StaffCreateRequest;
import com.example.beautysaas.dto.staff.StaffDto;
import com.example.beautysaas.dto.staff.StaffUpdateRequest;
import com.example.beautysaas.service.StaffService;
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
@RequestMapping("/admin/staff")
@Tag(name = "Staff Management", description = "APIs for managing beauty parlour staff, advance payments, and salaries")
@Slf4j
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @Operation(summary = "Add Staff", description = "Allows an Admin to add a new staff member for their parlour.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StaffDto> addStaff(
            Principal principal,
            @Parameter(description = "ID of the parlour to associate the staff with", required = true) @RequestParam UUID parlourId,
            @Valid @RequestBody StaffCreateRequest createRequest) {
        log.info("Admin {} adding staff for parlour {}: {}", principal.getName(), parlourId, createRequest.getName());
        StaffDto staff = staffService.addStaff(principal.getName(), parlourId, createRequest);
        return new ResponseEntity<>(staff, HttpStatus.CREATED);
    }

    @Operation(summary = "List Staff", description = "Retrieves a paginated list of staff members for an Admin's parlour.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<StaffDto>> listStaff(
            Principal principal,
            @Parameter(description = "ID of the parlour to retrieve staff from", required = true) @RequestParam UUID parlourId,
            Pageable pageable) {
        log.debug("Admin {} listing staff for parlour {}.", principal.getName(), parlourId);
        Page<StaffDto> staff = staffService.listStaff(principal.getName(), parlourId, pageable);
        return ResponseEntity.ok(staff);
    }

    @Operation(summary = "Get Staff Detail", description = "Retrieves a single staff member by ID for an Admin's parlour.")
    @GetMapping("/\{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StaffDto> getStaffDetail(Principal principal, @PathVariable UUID id) {
        log.debug("Admin {} fetching staff by ID: {}", principal.getName(), id);
        StaffDto staff = staffService.getStaffDetail(principal.getName(), id);
        return ResponseEntity.ok(staff);
    }

    @Operation(summary = "Update Staff", description = "Allows an Admin to update an existing staff member for their parlour.")
    @PutMapping("/\{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StaffDto> updateStaff(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody StaffUpdateRequest updateRequest) {
        log.info("Admin {} updating staff {}.", principal.getName(), id);
        StaffDto updatedStaff = staffService.updateStaff(principal.getName(), id, updateRequest);
        return ResponseEntity.ok(updatedStaff);
    }

    @Operation(summary = "Delete Staff", description = "Allows an Admin to delete a staff member from their parlour.")
    @DeleteMapping("/\{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStaff(Principal principal, @PathVariable UUID id) {
        log.info("Admin {} deleting staff {}.", principal.getName(), id);
        staffService.deleteStaff(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add Advance Payment", description = "Allows an Admin to record an advance payment for a staff member.")
    @PostMapping("/\{id}/advance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdvancePaymentDto> addAdvancePayment(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody AdvancePaymentCreateRequest createRequest) {
        log.info("Admin {} adding advance payment for staff {}.", principal.getName(), id);
        AdvancePaymentDto advancePayment = staffService.addAdvancePayment(principal.getName(), id, createRequest);
        return new ResponseEntity<>(advancePayment, HttpStatus.CREATED);
    }

    @Operation(summary = "Calculate & Record Salary", description = "Allows an Admin to calculate and record salary for a staff member for a given month/year.")
    @PostMapping("/\{id}/salary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalaryLogDto> calculateAndRecordSalary(
            Principal principal,
            @PathVariable UUID id,
            @Parameter(description = "Month (1-12)", required = true) @RequestParam int month,
            @Parameter(description = "Year", required = true) @RequestParam int year) {
        log.info("Admin {} calculating and recording salary for staff {} for {}-{}.", principal.getName(), id, month, year);
        SalaryLogDto salaryLog = staffService.calculateAndRecordSalary(principal.getName(), id, month, year);
        return new ResponseEntity<>(salaryLog, HttpStatus.CREATED);
    }

    @Operation(summary = "Get Salary Logs", description = "Retrieves a paginated list of salary logs for a staff member.")
    @GetMapping("/\{id}/salary-log")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SalaryLogDto>> getSalaryLogs(
            Principal principal,
            @PathVariable UUID id,
            Pageable pageable) {
        log.debug("Admin {} fetching salary logs for staff {}.", principal.getName(), id);
        Page<SalaryLogDto> salaryLogs = staffService.getSalaryLogs(principal.getName(), id, pageable);
        return ResponseEntity.ok(salaryLogs);
    }
}
