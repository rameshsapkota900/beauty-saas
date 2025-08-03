package com.example.beauty_saas.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductUpdateRequest {
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @DecimalMin(value = "0.00", message = "Price must be non-negative")
    private BigDecimal price;

    @DecimalMin(value = "0", message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    private UUID categoryId;
}
