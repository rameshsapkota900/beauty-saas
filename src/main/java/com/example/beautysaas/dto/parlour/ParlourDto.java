package com.example.beautysaas.dto.parlour;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ParlourDto {
    private UUID id;
    private String name;
    private String slug;
    private String address;
    private String phoneNumber;
    private String contactEmail;
    private UUID adminUserId; // ID of the associated admin user
    private String adminUserName;
    private String adminUserEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
