package com.example.beautysaas.controller;

import com.example.beautysaas.dto.product.ProductCreateRequest;
import com.example.beautysaas.dto.product.ProductDto;
import com.example.beautysaas.dto.product.ProductUpdateRequest;
import com.example.beautysaas.service.ProductService;
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
@Tag(name = "Beauty Product Management", description = "APIs for managing beauty parlour products")
@Slf4j
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Add Product", description = "Allows an Admin to add a new product for their parlour.")
    @PostMapping("/admin/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> addProduct(
            Principal principal,
            @Parameter(description = "ID of the parlour to associate the product with", required = true) @RequestParam UUID parlourId,
            @Valid @RequestBody ProductCreateRequest createRequest) {
        log.info("Admin {} adding product for parlour {}: {}", principal.getName(), parlourId, createRequest.getName());
        ProductDto product = productService.addProduct(principal.getName(), parlourId, createRequest);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @Operation(summary = "List Products (Public)", description = "Retrieves a paginated list of products for a specific parlour. Accessible publicly.")
    @GetMapping("/products")
    public ResponseEntity<Page<ProductDto>> listProducts(
            @Parameter(description = "ID of the parlour to retrieve products from", required = true) @RequestParam UUID parlourId,
            Pageable pageable) {
        log.debug("Listing products for parlour {}.", parlourId);
        Page<ProductDto> products = productService.listProducts(parlourId, pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get Product Detail", description = "Retrieves a single product by its ID. Accessible publicly.")
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> getProductDetail(@PathVariable UUID id) {
        log.debug("Fetching product by ID: {}", id);
        ProductDto product = productService.getProductDetail(id);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Update Product", description = "Allows an Admin to update an existing product for their parlour.")
    @PutMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(
            Principal principal,
            @PathVariable UUID id,
            @Valid @RequestBody ProductUpdateRequest updateRequest) {
        log.info("Admin {} updating product {}.", principal.getName(), id);
        ProductDto updatedProduct = productService.updateProduct(principal.getName(), id, updateRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Delete Product", description = "Allows an Admin to delete a product from their parlour.")
    @DeleteMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(Principal principal, @PathVariable UUID id) {
        log.info("Admin {} deleting product {}.", principal.getName(), id);
        productService.deleteProduct(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
