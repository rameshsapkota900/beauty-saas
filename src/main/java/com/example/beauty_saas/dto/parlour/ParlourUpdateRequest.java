package com.example.beauty_saas.dto.parlour;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ParlourUpdateRequest {
    @Size(min = 2, max = 100, message = "Parlour name must be between 2 and 100 characters")
    private String name;

    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must be lowercase, alphanumeric, and can contain hyphens")
    @Size(min = 3, max = 50, message = "Slug must be between 3 and 50 characters")
    private String slug;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}
