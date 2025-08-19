package com.beautyparlour.controller;

import com.beautyparlour.dto.request.CreateStaffRequest;
import com.beautyparlour.dto.request.StaffAdvancePayRequest;
import com.beautyparlour.dto.response.ApiResponse;
import com.beautyparlour.entity.Staff;
import com.beautyparlour.entity.StaffAdvancePay;
import com.beautyparlour.entity.StaffSalaryLog;
import com.beautyparlour.security.UserPrincipal;
import com.beautyparlour.service.StaffService;
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
@RequestMapping("/staffs")
@Tag(name = "Staff", description = "Staff management APIs")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @PostMapping
    @Operation(summary = "Add new staff")
    public ResponseEntity<ApiResponse<Staff>> createStaff(
            @Valid @RequestBody CreateStaffRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Staff staff = staffService.createStaff(request, currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Staff added successfully", staff));
    }

    @GetMapping
    @Operation(summary = "Get all staff")
    public ResponseEntity<ApiResponse<List<Staff>>> getAllStaff(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<Staff> staffList = staffService.getStaffByParlour(currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Staff retrieved successfully", staffList));
    }

    @DeleteMapping("/{staffId}")
    @Operation(summary = "Delete staff")
    public ResponseEntity<ApiResponse<Void>> deleteStaff(
            @PathVariable UUID staffId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        staffService.deleteStaff(staffId, currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Staff deleted successfully"));
    }

    @PostMapping("/{staffId}/advance")
    @Operation(summary = "Record advance pay for staff")
    public ResponseEntity<ApiResponse<StaffAdvancePay>> recordAdvancePay(
            @PathVariable UUID staffId,
            @Valid @RequestBody StaffAdvancePayRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        StaffAdvancePay advancePay = staffService.recordAdvancePay(staffId, request, currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Advance pay recorded successfully", advancePay));
    }

    @PostMapping("/{staffId}/salary")
    @Operation(summary = "Calculate and log salary")
    public ResponseEntity<ApiResponse<StaffSalaryLog>> calculateSalary(
            @PathVariable UUID staffId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        StaffSalaryLog salaryLog = staffService.calculateAndLogSalary(staffId, currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Salary calculated and logged successfully", salaryLog));
    }

    @GetMapping("/{staffId}/salary-log")
    @Operation(summary = "Get staff salary log")
    public ResponseEntity<ApiResponse<List<StaffSalaryLog>>> getStaffSalaryLog(
            @PathVariable UUID staffId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<StaffSalaryLog> salaryLogs = staffService.getStaffSalaryLog(staffId, currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Salary log retrieved successfully", salaryLogs));
    }
}
