package com.example.beauty_saas.controller;

import com.example.beautysaas.dto.category.CategoryCreateRequest;
import com.example.beautysaas.dto.category.CategoryDto;
import com.example.beautysaas.dto.category.CategoryUpdateRequest;
import com.example.beautysaas.service.CategoryService;
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
@RequestMapping("/api/admin/{parlourId}/categories")
@Tag(name = "Category Management", description = "APIs for managing service and product categories by Parlour Admin")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Create a new category", description = "Creates a new category for services or products within a specific parlour.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> createCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Valid @RequestBody CategoryCreateRequest createRequest) {
        log.info("Admin {} attempting to create category for parlour {}.", userDetails.getUsername(), parlourId);
        CategoryDto category = categoryService.createCategory(userDetails.getUsername(), parlourId, createRequest);
        log.info("Category created successfully with ID: {}", category.getId());
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @Operation(summary = "Get category by ID", description = "Retrieves a category by its ID for a specific parlour.")
    @GetMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<CategoryDto> getCategoryById(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the category to retrieve", required = true) @PathVariable UUID categoryId) {
        log.info("User {} fetching category {} for parlour {}.", userDetails.getUsername(), categoryId, parlourId);
        CategoryDto category = categoryService.getCategoryById(userDetails.getUsername(), parlourId, categoryId);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Get all categories for a parlour", description = "Retrieves a paginated list of all categories for a specific parlour.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Page<CategoryDto>> getAllCategories(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort order (e.g., 'name,asc')", example = "name,asc") @RequestParam(defaultValue = "name,asc") String[] sort) {
        log.info("User {} fetching all categories for parlour {}.", userDetails.getUsername(), parlourId);
        Pageable pageable = PaginationUtil.createPageable(page, size, sort);
        Page<CategoryDto> categories = categoryService.getAllCategories(userDetails.getUsername(), parlourId, pageable);
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Update a category", description = "Updates an existing category within a specific parlour.")
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> updateCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the category to update", required = true) @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryUpdateRequest updateRequest) {
        log.info("Admin {} attempting to update category {} for parlour {}.", userDetails.getUsername(), categoryId, parlourId);
        CategoryDto updatedCategory = categoryService.updateCategory(userDetails.getUsername(), parlourId, categoryId, updateRequest);
        log.info("Category {} updated successfully.", categoryId);
        return ResponseEntity.ok(updatedCategory);
    }

    @Operation(summary = "Delete a category", description = "Deletes a category from a specific parlour.")
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the category to delete", required = true) @PathVariable UUID categoryId) {
        log.info("Admin {} attempting to delete category {} from parlour {}.", userDetails.getUsername(), categoryId, parlourId);
        categoryService.deleteCategory(userDetails.getUsername(), parlourId, categoryId);
        log.info("Category {} deleted successfully.", categoryId);
        return ResponseEntity.ok("Category deleted successfully.");
    }
}
