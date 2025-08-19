package com.beautyparlour.controller;

import com.beautyparlour.dto.request.CreateCategoryRequest;
import com.beautyparlour.dto.response.ApiResponse;
import com.beautyparlour.entity.Category;
import com.beautyparlour.security.UserPrincipal;
import com.beautyparlour.service.CategoryService;
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
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Category management APIs")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create new category")
    public ResponseEntity<ApiResponse<Category>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Category category = categoryService.createCategory(request, currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Category created successfully", category));
    }

    @GetMapping
    @Operation(summary = "Get categories")
    public ResponseEntity<ApiResponse<List<Category>>> getCategories(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<Category> categories;
        if (currentUser != null && currentUser.getParlourId() != null) {
            categories = categoryService.getCategoriesByParlour(currentUser.getParlourId());
        } else {
            categories = categoryService.getAllCategories();
        }
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Delete category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable UUID categoryId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        categoryService.deleteCategory(categoryId, currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }
}
