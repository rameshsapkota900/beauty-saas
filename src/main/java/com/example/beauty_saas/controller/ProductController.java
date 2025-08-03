package com.example.beauty_saas.controller;

import com.example.beautysaas.dto.product.ProductCreateRequest;
import com.example.beautysaas.dto.product.ProductDto;
import com.example.beautysaas.dto.product.ProductUpdateRequest;
import com.example.beautysaas.service.ProductService;
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
@RequestMapping("/api/admin/{parlourId}/products")
@Tag(name = "Product Management", description = "APIs for managing beauty products by Parlour Admin")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create a new product", description = "Creates a new beauty product within a specific parlour.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> createProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Valid @RequestBody ProductCreateRequest createRequest) {
        log.info("Admin {} attempting to create product for parlour {}.", userDetails.getUsername(), parlourId);
        ProductDto product = productService.createProduct(userDetails.getUsername(), parlourId, createRequest);
        log.info("Product created successfully with ID: {}", product.getId());
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @Operation(summary = "Get product by ID", description = "Retrieves a product by its ID for a specific parlour.")
    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<ProductDto> getProductById(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the product to retrieve", required = true) @PathVariable UUID productId) {
        log.info("User {} fetching product {} for parlour {}.", userDetails.getUsername(), productId, parlourId);
        ProductDto product = productService.getProductById(userDetails.getUsername(), parlourId, productId);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Get all products for a parlour", description = "Retrieves a paginated list of all products for a specific parlour.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort order (e.g., 'name,asc')", example = "name,asc") @RequestParam(defaultValue = "name,asc") String[] sort) {
        log.info("User {} fetching all products for parlour {}.", userDetails.getUsername(), parlourId);
        Pageable pageable = PaginationUtil.createPageable(page, size, sort);
        Page<ProductDto> products = productService.getAllProducts(userDetails.getUsername(), parlourId, pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Update a product", description = "Updates an existing product within a specific parlour.")
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the product to update", required = true) @PathVariable UUID productId,
            @Valid @RequestBody ProductUpdateRequest updateRequest) {
        log.info("Admin {} attempting to update product {} for parlour {}.", userDetails.getUsername(), productId, parlourId);
        ProductDto updatedProduct = productService.updateProduct(userDetails.getUsername(), parlourId, productId, updateRequest);
        log.info("Product {} updated successfully.", productId);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Delete a product", description = "Deletes a product from a specific parlour.")
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID of the parlour", required = true) @PathVariable UUID parlourId,
            @Parameter(description = "ID of the product to delete", required = true) @PathVariable UUID productId) {
        log.info("Admin {} attempting to delete product {} from parlour {}.", userDetails.getUsername(), productId, parlourId);
        productService.deleteProduct(userDetails.getUsername(), parlourId, productId);
        log.info("Product {} deleted successfully.", productId);
        return ResponseEntity.ok("Product deleted successfully.");
    }
}
