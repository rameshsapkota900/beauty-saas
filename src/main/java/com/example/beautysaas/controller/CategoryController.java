package com.example.beautysaas.controller;

import com.example.beautysaas.dto.category.CategoryCreateRequest;
import com.example.beautysaas.dto.category.CategoryDto;
import com.example.beautysaas.dto.category.CategoryUpdateRequest;
import com.example.beautysaas.dto.category.CategoryTreeDto;
import com.example.beautysaas.service.CategoryService;
import com.example.beautysaas.service.CategoryTreeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Category Management", description = "APIs for managing beauty parlour categories and their hierarchy")
@Slf4j
@CrossOrigin(origins = {"${app.cors.allowed-origins}"}, maxAge = 3600)
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryTreeService categoryTreeService;

    public CategoryController(CategoryService categoryService, CategoryTreeService categoryTreeService) {
        this.categoryService = categoryService;
        this.categoryTreeService = categoryTreeService;
    }

    @Operation(summary = "Create Category", description = "Allows an Admin to create a new category for their parlour.")
    @PostMapping("/admin/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> createCategory(
            Principal principal,
            @Parameter(description = "ID of the parlour to associate the category with", required = true) @RequestParam UUID parlourId,
            @Valid @RequestBody CategoryCreateRequest createRequest) {
        log.info("Admin {} creating category for parlour {}: {}", principal.getName(), parlourId, createRequest.getName());
        CategoryDto category = categoryService.createCategory(principal.getName(), parlourId, createRequest);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @Operation(summary = "List Categories (Public)", description = "Retrieves a paginated list of categories for a specific parlour. Accessible publicly.")
    @GetMapping("/categories")
    public ResponseEntity<Page<CategoryDto>> listCategories(
            @Parameter(description = "ID of the parlour to retrieve categories from", required = true) @RequestParam UUID parlourId,
            Pageable pageable) {
        log.debug("Listing categories for parlour {}.", parlourId);
        Page<CategoryDto> categories = categoryService.listCategories(parlourId, pageable);
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Get Category by ID", description = "Retrieves a single category by its ID. Accessible publicly.")
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable UUID id) {
        log.debug("Fetching category by ID: {}", id);
        CategoryDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Update Category", description = "Allows an Admin to update an existing category for their parlour.")
    @PutMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> updateCategory(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody CategoryUpdateRequest updateRequest) {
        log.info("Admin {} updating category {}.", principal.getName(), id);
        CategoryDto updatedCategory = categoryService.updateCategory(principal.getName(), id, updateRequest);
        return ResponseEntity.ok(updatedCategory);
    }

    @Operation(summary = "Delete Category", description = "Allows an Admin to delete a category from their parlour.")
    @DeleteMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(Principal principal, @PathVariable UUID id) {
        log.info("Admin {} deleting category {}.", principal.getName(), id);
        categoryService.deleteCategory(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
