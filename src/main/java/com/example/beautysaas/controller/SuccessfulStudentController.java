package com.example.beautysaas.controller;

import com.example.beautysaas.dto.successfulstudent.SuccessfulStudentCreateRequest;
import com.example.beautysaas.dto.successfulstudent.SuccessfulStudentDto;
import com.example.beautysaas.dto.successfulstudent.SuccessfulStudentUpdateRequest;
import com.example.beautysaas.service.SuccessfulStudentService;
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
@Tag(name = "Successful Student Management", description = "APIs for managing successful students")
@Slf4j
public class SuccessfulStudentController {

    private final SuccessfulStudentService successfulStudentService;

    public SuccessfulStudentController(SuccessfulStudentService successfulStudentService) {
        this.successfulStudentService = successfulStudentService;
    }

    @Operation(summary = "Add Successful Student", description = "Allows an Admin to add a new successful student for their parlour.")
    @PostMapping("/admin/successful-students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessfulStudentDto> addSuccessfulStudent(
            Principal principal,
            @Parameter(description = "ID of the parlour to associate the student with", required = true) @RequestParam UUID parlourId,
            @Valid @RequestBody SuccessfulStudentCreateRequest createRequest) {
        log.info("Admin {} adding successful student for parlour {}: {}", principal.getName(), parlourId, createRequest.getName());
        SuccessfulStudentDto student = successfulStudentService.addSuccessfulStudent(principal.getName(), parlourId, createRequest);
        return new ResponseEntity<>(student, HttpStatus.CREATED);
    }

    @Operation(summary = "List Successful Students (Public)", description = "Retrieves a paginated list of successful students for a specific parlour. Accessible publicly.")
    @GetMapping("/successful-students")
    public ResponseEntity<Page<SuccessfulStudentDto>> listSuccessfulStudents(
            @Parameter(description = "ID of the parlour to retrieve students from", required = true) @RequestParam UUID parlourId,
            Pageable pageable) {
        log.debug("Listing successful students for parlour {}.", parlourId);
        Page<SuccessfulStudentDto> students = successfulStudentService.listSuccessfulStudents(parlourId, pageable);
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "Get Student Detail", description = "Retrieves a single successful student by its ID. Accessible publicly.")
    @GetMapping("/successful-students/\{id}")
    public ResponseEntity<SuccessfulStudentDto> getStudentDetail(@PathVariable UUID id) {
        log.debug("Fetching successful student by ID: {}", id);
        SuccessfulStudentDto student = successfulStudentService.getStudentDetail(id);
        return ResponseEntity.ok(student);
    }

    @Operation(summary = "Update Student", description = "Allows an Admin to update an existing successful student for their parlour.")
    @PutMapping("/admin/successful-students/\{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessfulStudentDto> updateSuccessfulStudent(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody SuccessfulStudentUpdateRequest updateRequest) {
        log.info("Admin {} updating successful student {}.", principal.getName(), id);
        SuccessfulStudentDto updatedStudent = successfulStudentService.updateSuccessfulStudent(principal.getName(), id, updateRequest);
        return ResponseEntity.ok(updatedStudent);
    }

    @Operation(summary = "Delete Student", description = "Allows an Admin to delete a successful student from their parlour.")
    @DeleteMapping("/admin/successful-students/\{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSuccessfulStudent(Principal principal, @PathVariable UUID id) {
        log.info("Admin {} deleting successful student {}.", principal.getName(), id);
        successfulStudentService.deleteSuccessfulStudent(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
