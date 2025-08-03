package com.example.beautysaas.controller;

import com.example.beautysaas.dto.course.CourseCreateRequest;
import com.example.beautysaas.dto.course.CourseDto;
import com.example.beautysaas.dto.course.CourseUpdateRequest;
import com.example.beautysaas.service.CourseService;
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
@Tag(name = "Course Management", description = "APIs for managing beauty parlour courses")
@Slf4j
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(summary = "Add Course", description = "Allows an Admin to add a new course for their parlour.")
    @PostMapping("/admin/courses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDto> addCourse(
            Principal principal,
            @Parameter(description = "ID of the parlour to associate the course with", required = true) @RequestParam UUID parlourId,
            @Valid @RequestBody CourseCreateRequest createRequest) {
        log.info("Admin {} adding course for parlour {}: {}", principal.getName(), parlourId, createRequest.getName());
        CourseDto course = courseService.addCourse(principal.getName(), parlourId, createRequest);
        return new ResponseEntity<>(course, HttpStatus.CREATED);
    }

    @Operation(summary = "List Courses (Public)", description = "Retrieves a paginated list of courses for a specific parlour. Accessible publicly.")
    @GetMapping("/courses")
    public ResponseEntity<Page<CourseDto>> listCourses(
            @Parameter(description = "ID of the parlour to retrieve courses from", required = true) @RequestParam UUID parlourId,
            Pageable pageable) {
        log.debug("Listing courses for parlour {}.", parlourId);
        Page<CourseDto> courses = courseService.listCourses(parlourId, pageable);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Get Course Detail", description = "Retrieves a single course by its ID. Accessible publicly.")
    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseDto> getCourseDetail(@PathVariable UUID id){
        log.debug("Fetching course by ID: {}", id);
        CourseDto course = courseService.getCourseDetail(id);
        return ResponseEntity.ok(course);
    }

    @Operation(summary = "Update Course", description = "Allows an Admin to update an existing course for their parlour.")
    @PutMapping("/admin/courses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDto> updateCourse(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody CourseUpdateRequest updateRequest) {
        log.info("Admin {} updating course {}.", principal.getName(), id);
        CourseDto updatedCourse = courseService.updateCourse(principal.getName(), id, updateRequest);
        return ResponseEntity.ok(updatedCourse);
    }

    @Operation(summary = "Delete Course", description = "Allows an Admin to delete a course from their parlour.")
    @DeleteMapping("/admin/courses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(Principal principal, @PathVariable UUID id) {
        log.info("Admin {} deleting course {}.", principal.getName(), id);
        courseService.deleteCourse(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
