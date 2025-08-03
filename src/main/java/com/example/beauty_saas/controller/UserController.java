package com.example.beauty_saas.controller;

import com.example.beautysaas.dto.user.UserDto;
import com.example.beautysaas.dto.user.UserProfileUpdateRequest;
import com.example.beautysaas.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Profile Management", description = "APIs for managing user profiles (Admin, Customer)")
@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get current user profile", description = "Retrieves the profile of the authenticated user.")
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<UserDto> getCurrentUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Fetching profile for user: {}", userDetails.getUsername());
        UserDto userDto = userService.getUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Update current user profile", description = "Updates the profile of the authenticated user.")
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<UserDto> updateCurrentUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserProfileUpdateRequest updateRequest) {
        log.info("Updating profile for user: {}", userDetails.getUsername());
        UserDto updatedUser = userService.updateUserProfile(userDetails.getUsername(), updateRequest);
        log.info("Profile updated successfully for user: {}", userDetails.getUsername());
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete current user account", description = "Deletes the account of the authenticated user. This action is irreversible.")
    @DeleteMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')") // SuperAdmin accounts should not be deleted via this endpoint
    public ResponseEntity<String> deleteCurrentUserAccount(@AuthenticationPrincipal UserDetails userDetails) {
        log.warn("Attempting to delete account for user: {}. This action is irreversible.", userDetails.getUsername());
        userService.deleteUserAccount(userDetails.getUsername());
        log.info("Account deleted successfully for user: {}", userDetails.getUsername());
        return ResponseEntity.ok("Account deleted successfully.");
    }
}
