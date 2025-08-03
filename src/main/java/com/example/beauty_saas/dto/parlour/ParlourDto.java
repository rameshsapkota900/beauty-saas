package com.example.beauty_saas.dto.parlour;

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
    private String email;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
