package com.beautyparlour.controller;

import com.beautyparlour.dto.request.CreateCourseRequest;
import com.beautyparlour.dto.response.ApiResponse;
import com.beautyparlour.entity.Course;
import com.beautyparlour.security.UserPrincipal;
import com.beautyparlour.service.CourseService;
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
@RequestMapping("/courses")
@Tag(name = "Courses", description = "Course management APIs")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    @Operation(summary = "Create new course")
    public ResponseEntity<ApiResponse<Course>> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Course course = courseService.createCourse(request, currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Course created successfully", course));
    }

    @GetMapping
    @Operation(summary = "Get courses")
    public ResponseEntity<ApiResponse<List<Course>>> getCourses(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<Course> courses;
        if (currentUser != null && currentUser.getParlourId() != null) {
            courses = courseService.getCoursesByParlour(currentUser.getParlourId());
        } else {
            courses = courseService.getAllCourses();
        }
        return ResponseEntity.ok(ApiResponse.success("Courses retrieved successfully", courses));
    }

    @DeleteMapping("/{courseId}")
    @Operation(summary = "Delete course")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        courseService.deleteCourse(courseId, currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Course deleted successfully"));
    }
}
