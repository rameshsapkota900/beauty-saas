package com.example.beauty_saas.dto.user;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private String role;
    private UUID parlourId; // Null for SuperAdmin and Customers not tied to a specific parlour
    private String parlourName; // Null for SuperAdmin and Customers not tied to a specific parlour
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
