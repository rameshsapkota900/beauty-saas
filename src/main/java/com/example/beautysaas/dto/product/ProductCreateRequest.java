package com.example.beautysaas.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductCreateRequest {
    @NotBlank(message = "Product name cannot be empty")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    private String description;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock quantity cannot be null")
    @DecimalMin(value = "0", message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotNull(message = "Category ID cannot be null")
    private UUID categoryId;

    @NotNull(message = "Is active cannot be null")
    private Boolean isActive;
}
