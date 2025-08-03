package com.example.beautysaas.dto.parlour;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ParlourCreateRequest {
    @NotBlank(message = "Parlour name cannot be empty")
    @Size(min = 2, max = 100, message = "Parlour name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Parlour slug cannot be empty")
    @Size(min = 2, max = 50, message = "Parlour slug must be between 2 and 50 characters")
    private String slug; // Unique identifier for the parlour in URLs

    @NotBlank(message = "Address cannot be empty")
    private String address;

    @NotBlank(message = "Phone number cannot be empty")
    private String phoneNumber;

    @Email(message = "Contact email should be valid")
    private String contactEmail;

    @Valid // Enable validation for nested AdminUserRequest
    private AdminUserRequest adminUser;

    @Data
    public static class AdminUserRequest {
        @NotBlank(message = "Admin name cannot be empty")
        @Size(min = 2, max = 100, message = "Admin name must be between 2 and 100 characters")
        private String name;

        @NotBlank(message = "Admin email cannot be empty")
        @Email(message = "Admin email should be valid")
        private String email;

        @NotBlank(message = "Admin password cannot be empty")
        @Size(min = 6, message = "Admin password must be at least 6 characters long")
        private String password;
    }
}
