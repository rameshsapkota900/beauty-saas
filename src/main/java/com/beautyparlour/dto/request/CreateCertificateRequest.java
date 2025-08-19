package com.beautyparlour.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateCertificateRequest {
    @NotBlank(message = "Student name is required")
    private String studentName;

    private String imageUrl;

    // Constructors
    public CreateCertificateRequest() {}

    // Getters and Setters
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
