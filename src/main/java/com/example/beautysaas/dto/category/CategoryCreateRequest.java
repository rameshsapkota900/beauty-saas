package com.example.beautysaas.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryCreateRequest {
    @NotBlank(message = "Category name cannot be empty")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    private String description;
}
