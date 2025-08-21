package com.beautyparlour.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for all DTOs with common audit fields
 */
public abstract class BaseDTO {
    protected UUID id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public BaseDTO() {}

    public BaseDTO(UUID id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public BaseDTO(UUID id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
