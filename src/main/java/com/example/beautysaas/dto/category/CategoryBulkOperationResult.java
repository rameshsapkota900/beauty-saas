package com.example.beautysaas.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Result of a bulk operation on categories")
public class CategoryBulkOperationResult {
    
    @Schema(description = "Number of categories successfully processed")
    private int successCount;
    
    @Schema(description = "Number of categories that failed to process")
    private int failureCount;
    
    @Schema(description = "Total number of categories in the operation")
    private int totalCount;
    
    @Schema(description = "List of error messages for failed operations")
    private List<String> errors;
    
    @Schema(description = "Timestamp when the operation completed")
    private LocalDateTime completedAt;
    
    @Schema(description = "Whether the operation was completely successful")
    public boolean isCompletelySuccessful() {
        return failureCount == 0;
    }
    
    @Schema(description = "Success rate as a percentage")
    public double getSuccessRate() {
        return totalCount > 0 ? (successCount * 100.0) / totalCount : 0.0;
    }
}
