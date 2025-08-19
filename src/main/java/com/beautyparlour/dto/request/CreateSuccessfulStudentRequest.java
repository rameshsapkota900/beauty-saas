package com.beautyparlour.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateSuccessfulStudentRequest {
    @NotBlank(message = "Student name is required")
    private String name;

    private String imageUrl;

    // Constructors
    public CreateSuccessfulStudentRequest() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
