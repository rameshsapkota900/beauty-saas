
package com.example.beautysaas.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for password change requests.
 * Used to validate and transfer password change data from client to server.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {
    /**
     * The user's current password.
     */
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    /**
     * The new password the user wants to set.
     */
    @NotBlank(message = "New password is required")
    private String newPassword;

    /**
     * Confirmation of the new password.
     */
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
    
    /**
     * Utility method to check if newPassword and confirmPassword match.
     */
    public boolean isNewPasswordConfirmed() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}
