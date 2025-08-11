package com.example.beautysaas.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Builder.Default
    private boolean active = true;

    private int displayOrder;

    @Size(max = 255, message = "Icon URL cannot exceed 255 characters")
    private String iconUrl;

    @Size(min = 4, max = 7, message = "Color code must be between 4 and 7 characters")
    private String colorCode;

    @Size(max = 255, message = "Meta keywords cannot exceed 255 characters")
    private String metaKeywords;

    @Size(max = 500, message = "Meta description cannot exceed 500 characters")
    private String metaDescription;
}
