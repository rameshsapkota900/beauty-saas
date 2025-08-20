package com.beautyparlour.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateServiceRequest {
    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @NotBlank(message = "Service name is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\s.-]{2,100}$", message = "Service name must be 2-100 characters and contain only letters, numbers, spaces, dots, and hyphens")
    private String name;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}(/.*)?$|^$", message = "Image URL must be a valid URL or empty")
    private String imageUrl;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    // Constructors
    public CreateServiceRequest() {}

    // Getters and Setters
    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
