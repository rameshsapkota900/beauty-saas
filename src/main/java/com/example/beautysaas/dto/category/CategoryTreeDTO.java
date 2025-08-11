package com.example.beautysaas.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeDTO {
    private UUID id;
    private String name;
    private String description;
    private boolean active;
    private int displayOrder;
    private String iconUrl;
    private String colorCode;
    private String metaKeywords;
    private String metaDescription;
    private Integer level;
    private String path;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CategoryTreeDTO> children;
}
