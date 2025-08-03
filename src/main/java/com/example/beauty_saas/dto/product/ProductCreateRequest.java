package com.example.beauty_saas.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductCreateRequest {
    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.00", message = "Price must be non-negative")
    private BigDecimal price;

    @NotNull(message = "Stock quantity cannot be null")
    @DecimalMin(value = "0", message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotNull(message = "Category ID cannot be null")
    private UUID categoryId;
}
