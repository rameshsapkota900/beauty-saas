package com.example.beautysaas.dto.category;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatsDtoFixed {
    private Long totalCategories;
    private Long activeCategories;
    private Long deletedCategories;
    private Integer maxLevel;
    
    @Builder.Default
    private Map<Integer, Long> categoriesPerLevel = new HashMap<>();
    
    private Long rootCategories;
    private Long leafCategories;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;
    
    public double getActivePercentage() {
        return totalCategories > 0 ? (activeCategories * 100.0) / totalCategories : 0.0;
    }
    
    public boolean hasCategories() {
        return totalCategories != null && totalCategories > 0;
    }
    
    @JsonIgnore
    public int getAverageDepth() {
        if (categoriesPerLevel.isEmpty()) return 0;
        long totalDepth = 0;
        long totalCount = 0;
        for (Map.Entry<Integer, Long> entry : categoriesPerLevel.entrySet()) {
            totalDepth += entry.getKey() * entry.getValue();
            totalCount += entry.getValue();
        }
        return totalCount > 0 ? (int) (totalDepth / totalCount) : 0;
    }
}
