package com.example.beautysaas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "access_control_entries",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"user_email", "resource_type", "resource_id", "permission"}
       ))
public class AccessControlEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_email", nullable = false)
    private String userEmail;
    
    @Column(name = "resource_type", nullable = false)
    private String resourceType;
    
    @Column(name = "resource_id", nullable = false)
    private String resourceId;
    
    @Column(nullable = false)
    private String permission;
    
    @Column(name = "created_at", nullable = false)
    private java.time.LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}
