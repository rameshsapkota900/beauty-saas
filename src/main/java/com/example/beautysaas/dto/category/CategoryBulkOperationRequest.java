package com.example.beautysaas.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Bulk operation request for categories")
public class CategoryBulkOperationRequest {
    
    @Schema(description = "List of category IDs to operate on", required = true)
    private List<UUID> categoryIds;
    
    @Schema(description = "Type of bulk operation", allowableValues = {"ACTIVATE", "DEACTIVATE", "DELETE", "ARCHIVE"})
    private BulkOperationType operationType;
    
    @Schema(description = "Optional reason for the operation")
    private String reason;
    
    public enum BulkOperationType {
        ACTIVATE,
        DEACTIVATE,
        DELETE,
        ARCHIVE
    }
}
