package com.example.beautysaas.dto.user;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private String role; // e.g., SUPERADMIN, ADMIN, CUSTOMER
    private UUID parlourId; // Null for SUPERADMIN, present for ADMIN/CUSTOMER
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
