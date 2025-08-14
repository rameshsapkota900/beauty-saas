package com.example.beautysaas.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatsDto {
    private Long totalCategories;
    private Long activeCategories;
    private Long deletedCategories;
    private Integer maxLevel;
}
