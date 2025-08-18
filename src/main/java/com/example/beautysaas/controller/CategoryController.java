package com.example.beautysaas.controller;

import com.example.beautysaas.dto.category.CategoryCreateRequest;
import com.example.beautysaas.dto.category.CategoryDto;
import com.example.beautysaas.dto.category.CategoryReorderRequest;
import com.example.beautysaas.dto.category.CategoryStatsDto;
import com.example.beautysaas.dto.category.CategoryTreeDTO;
import com.example.beautysaas.dto.category.CategoryUpdateRequest;
import com.example.beautysaas.service.CategoryService;
import com.example.beautysaas.service.CategoryTreeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
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

    @Operation(summary = "Get Category Statistics", description = "Retrieves statistics about categories for a parlour.")
    @GetMapping("/admin/categories/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryStatsDto> getCategoryStatistics(
            Principal principal,
            @Parameter(description = "ID of the parlour to get statistics for", required = true)
            @RequestParam UUID parlourId) {
        log.info("Admin {} retrieving category statistics for parlour {}.", principal.getName(), parlourId);
        CategoryStatsDto stats = categoryService.getCategoryStatistics(principal.getName(), parlourId);
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Bulk Update Category Status", description = "Updates the active status of multiple categories.")
    @PutMapping("/admin/categories/bulk-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> bulkUpdateStatus(
            Principal principal,
            @Parameter(description = "ID of the parlour containing the categories", required = true)
            @RequestParam UUID parlourId,
            @Parameter(description = "List of category IDs to update", required = true)
            @RequestBody List<UUID> categoryIds,
            @Parameter(description = "New active status", required = true)
            @RequestParam boolean active) {
        log.info("Admin {} bulk updating {} categories status to {} for parlour {}.",
                principal.getName(), categoryIds.size(), active, parlourId);
        categoryService.bulkUpdateStatus(principal.getName(), parlourId, categoryIds, active);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Search Categories by Metadata", description = "Search categories using metadata fields like keywords and color code.")
    @GetMapping("/admin/categories/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CategoryDto>> searchByMetadata(
            Principal principal,
            @Parameter(description = "ID of the parlour", required = true)
            @RequestParam UUID parlourId,
            @Parameter(description = "Meta keywords to search for")
            @RequestParam(required = false) String metaKeywords,
            @Parameter(description = "Color code to filter by")
            @RequestParam(required = false) String colorCode,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Admin {} searching categories with metadata for parlour {}", principal.getName(), parlourId);
        return ResponseEntity.ok(categoryService.searchByMetadata(principal.getName(), parlourId, metaKeywords, colorCode, pageable));
    }

    @Operation(summary = "Get Categories by Path Depth", description = "Retrieve categories at a specific depth in the hierarchy.")
    @GetMapping("/admin/categories/by-depth/{depth}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategoryDto>> getCategoriesByDepth(
            Principal principal,
            @Parameter(description = "ID of the parlour", required = true)
            @RequestParam UUID parlourId,
            @Parameter(description = "Depth level in the category hierarchy", required = true)
            @PathVariable int depth) {
        log.info("Admin {} fetching categories at depth {} for parlour {}", principal.getName(), depth, parlourId);
        return ResponseEntity.ok(categoryService.getCategoriesByDepth(principal.getName(), parlourId, depth));
    }

    @Operation(summary = "Reorder Categories", description = "Update the display order of categories within the same parent.")
    @PutMapping("/admin/categories/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reorderCategories(
            Principal principal,
            @Parameter(description = "ID of the parlour", required = true)
            @RequestParam UUID parlourId,
            @Parameter(description = "Parent category ID", required = false)
            @RequestParam(required = false) UUID parentId,
            @Parameter(description = "Category reordering details", required = true)
            @Valid @RequestBody List<CategoryReorderRequest> reorderRequests) {
        log.info("Admin {} reordering categories under parent {} for parlour {}", 
                principal.getName(), parentId, parlourId);
        categoryService.reorderCategories(principal.getName(), parlourId, parentId, reorderRequests);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Category Tree", description = "Retrieve the full category tree for a parlour.")
    @GetMapping("/categories/tree")
    public ResponseEntity<List<CategoryTreeDTO>> getCategoryTree(
            @Parameter(description = "ID of the parlour", required = true)
            @RequestParam UUID parlourId) {
        log.info("Fetching category tree for parlour {}", parlourId);
        return ResponseEntity.ok(categoryTreeService.getCategoryTree(parlourId));
    }

    @Operation(summary = "Get Category Subtree", description = "Retrieve a subtree starting from a specific category.")
    @GetMapping("/categories/{categoryId}/subtree")
    public ResponseEntity<CategoryTreeDTO> getCategorySubtree(
            @Parameter(description = "ID of the root category for the subtree", required = true)
            @PathVariable UUID categoryId) {
        log.info("Fetching category subtree for category {}", categoryId);
        return ResponseEntity.ok(categoryTreeService.getCategorySubtree(categoryId));
    }

    @Operation(summary = "Search Category Tree", description = "Search categories and return matching tree structure.")
    @GetMapping("/admin/categories/tree/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategoryTreeDTO>> searchCategoryTree(
            Principal principal,
            @Parameter(description = "ID of the parlour", required = true)
            @RequestParam UUID parlourId,
            @Parameter(description = "Search term", required = true)
            @RequestParam String searchTerm) {
        log.info("Admin {} searching category tree for parlour {} with term: {}", 
                principal.getName(), parlourId, searchTerm);
        return ResponseEntity.ok(categoryTreeService.searchCategoryTree(parlourId, searchTerm));
    }

    @Operation(summary = "Get Categories by Level", description = "Retrieve categories at a specific hierarchy level.")
    @GetMapping("/categories/level/{level}")
    public ResponseEntity<List<CategoryTreeDTO>> getCategoriesByLevel(
            @Parameter(description = "ID of the parlour", required = true)
            @RequestParam UUID parlourId,
            @Parameter(description = "Hierarchy level", required = true)
            @PathVariable Integer level) {
        log.info("Fetching categories at level {} for parlour {}", level, parlourId);
        return ResponseEntity.ok(categoryTreeService.getCategoriesByLevel(parlourId, level));
    }
}
