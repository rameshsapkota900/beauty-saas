package com.example.beautysaas.dto.category;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryUpdateRequest {
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    private String description;
}
