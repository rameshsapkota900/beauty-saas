package com.example.beautysaas.dto.category;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CategoryDto {
    private UUID id;
    private UUID parlourId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
