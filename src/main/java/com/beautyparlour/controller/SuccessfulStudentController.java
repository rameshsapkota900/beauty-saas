package com.beautyparlour.controller;

import com.beautyparlour.dto.request.CreateSuccessfulStudentRequest;
import com.beautyparlour.dto.response.ApiResponse;
import com.beautyparlour.entity.SuccessfulStudent;
import com.beautyparlour.security.UserPrincipal;
import com.beautyparlour.service.SuccessfulStudentService;
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
@RequestMapping("/success-students")
@Tag(name = "Successful Students", description = "Successful student management APIs")
public class SuccessfulStudentController {

    @Autowired
    private SuccessfulStudentService successfulStudentService;

    @PostMapping
    @Operation(summary = "Add successful student")
    public ResponseEntity<ApiResponse<SuccessfulStudent>> createSuccessfulStudent(
            @Valid @RequestBody CreateSuccessfulStudentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        SuccessfulStudent student = successfulStudentService.createSuccessfulStudent(request, currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Successful student added successfully", student));
    }

    @GetMapping
    @Operation(summary = "Get successful students")
    public ResponseEntity<ApiResponse<List<SuccessfulStudent>>> getSuccessfulStudents(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<SuccessfulStudent> students;
        if (currentUser != null && currentUser.getParlourId() != null) {
            students = successfulStudentService.getSuccessfulStudentsByParlour(currentUser.getParlourId());
        } else {
            students = successfulStudentService.getAllSuccessfulStudents();
        }
        return ResponseEntity.ok(ApiResponse.success("Successful students retrieved successfully", students));
    }

    @DeleteMapping("/{studentId}")
    @Operation(summary = "Delete successful student")
    public ResponseEntity<ApiResponse<Void>> deleteSuccessfulStudent(
            @PathVariable UUID studentId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        successfulStudentService.deleteSuccessfulStudent(studentId, currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Successful student deleted successfully"));
    }
}
