package com.example.beauty_saas.controller;

import com.example.beautysaas.dto.course.CourseCreateRequest;
import com.example.beautysaas.dto.course.CourseDto;
import com.example.beautysaas.dto.course.CourseUpdateRequest;
import com.example.beautysaas.service.CourseService;
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
@RequestMapping("/api/admin/{parlourId}/courses")
@Tag(name = "Course Management", description = "APIs for managing courses by Parlour Admin")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(summary = "Create a new course", description = "Creates a new course within a specific parlour.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDto> createCourse(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Valid @RequestBody CourseCreateRequest createRequest) {
        log.info("Admin {} attempting to create course for parlour {}.", userDetails.getUsername(), parlourId);
        CourseDto course = courseService.createCourse(userDetails.getUsername(), parlourId, createRequest);
        log.info("Course created successfully with ID: {}", course.getId());
        return new ResponseEntity<>(course, HttpStatus.CREATED);
    }

    @Operation(summary = "Get course by ID", description = "Retrieves a course by its ID for a specific parlour.")
    @GetMapping("/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<CourseDto> getCourseById(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the course to retrieve", required = true) @PathVariable UUID courseId) {
        log.info("User {} fetching course {} for parlour {}.", userDetails.getUsername(), courseId, parlourId);
        CourseDto course = courseService.getCourseById(userDetails.getUsername(), parlourId, courseId);
        return ResponseEntity.ok(course);
    }

    @Operation(summary = "Get all courses for a parlour", description = "Retrieves a paginated list of all courses for a specific parlour.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Page<CourseDto>> getAllCourses(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort order (e.g., 'name,asc')", example = "name,asc") @RequestParam(defaultValue = "name,asc") String[] sort) {
        log.info("User {} fetching all courses for parlour {}.", userDetails.getUsername(), parlourId);
        Pageable pageable = PaginationUtil.createPageable(page, size, sort);
        Page<CourseDto> courses = courseService.getAllCourses(userDetails.getUsername(), parlourId, pageable);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Update a course", description = "Updates an existing course within a specific parlour.")
    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDto> updateCourse(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the course to update", required = true) @PathVariable UUID courseId,
            @Valid @RequestBody CourseUpdateRequest updateRequest) {
        log.info("Admin {} attempting to update course {} for parlour {}.", userDetails.getUsername(), courseId, parlourId);
        CourseDto updatedCourse = courseService.updateCourse(userDetails.getUsername(), parlourId, courseId, updateRequest);
        log.info("Course {} updated successfully.", courseId);
        return ResponseEntity.ok(updatedCourse);
    }

    @Operation(summary = "Delete a course", description = "Deletes a course from a specific parlour.")
    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCourse(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the course to delete", required = true) @PathVariable UUID courseId) {
        log.info("Admin {} attempting to delete course {} from parlour {}.", userDetails.getUsername(), courseId, parlourId);
        courseService.deleteCourse(userDetails.getUsername(), parlourId, courseId);
        log.info("Course {} deleted successfully.", courseId);
        return ResponseEntity.ok("Course deleted successfully.");
    }
}
