package com.example.beautysaas.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordChangeRequest {
    
    @NotBlank(message = "Current password cannot be empty")
    private String currentPassword;
    
    @NotBlank(message = "New password cannot be empty")
    private String newPassword;
    
    @NotBlank(message = "Confirm password cannot be empty")
    private String confirmPassword;
}
