package com.beautyparlour.dto.response;

import com.beautyparlour.entity.Course;

import java.math.BigDecimal;
import java.util.UUID;

public class CourseDTO {
    private UUID id;
    private UUID parlourId;
    private String name;
    private String imageUrl;
    private String description;
    private BigDecimal price;

    public CourseDTO() {
    }

    public CourseDTO(Course course) {
        this.id = course.getId();
        this.parlourId = course.getParlourId();
        this.name = course.getName();
        this.imageUrl = course.getImageUrl();
        this.description = course.getDescription();
        this.price = course.getPrice();
    }

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
