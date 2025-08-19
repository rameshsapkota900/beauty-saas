package com.beautyparlour.dto.response;

import com.beautyparlour.entity.Service;

import java.math.BigDecimal;
import java.util.UUID;

public class ServiceDTO {
    private UUID id;
    private UUID parlourId;
    private UUID categoryId;
    private String name;
    private String imageUrl;
    private BigDecimal price;
    private String categoryName;

    public ServiceDTO() {
    }

    public ServiceDTO(Service service) {
        this.id = service.getId();
        this.parlourId = service.getParlourId();
        this.categoryId = service.getCategoryId();
        this.name = service.getName();
        this.imageUrl = service.getImageUrl();
        this.price = service.getPrice();

        // Safely get category name only if category is loaded
        if (service.getCategory() != null) {
            this.categoryName = service.getCategory().getName();
        }
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getParlourId() {
        return parlourId;
    }

    public void setParlourId(UUID parlourId) {
        this.parlourId = parlourId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
