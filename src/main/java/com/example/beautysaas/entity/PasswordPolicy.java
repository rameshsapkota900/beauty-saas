package com.example.beautysaas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_policies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordPolicy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "min_length", nullable = false)
    @Builder.Default
    private int minLength = 8;
    
    @Column(name = "require_uppercase", nullable = false)
    @Builder.Default
    private boolean requireUppercase = true;
    
    @Column(name = "require_lowercase", nullable = false)
    @Builder.Default
    private boolean requireLowercase = true;
    
    @Column(name = "require_numbers", nullable = false)
    @Builder.Default
    private boolean requireNumbers = true;
    
    @Column(name = "require_special_chars", nullable = false)
    @Builder.Default
    private boolean requireSpecialChars = true;
    
    @Column(name = "max_age_days", nullable = false)
    @Builder.Default
    private int maxAgeDays = 90;
    
    @Column(name = "history_count", nullable = false)
    @Builder.Default
    private int historyCount = 5;
    
    @Column(name = "min_unique_chars", nullable = false)
    @Builder.Default
    private int minUniqueChars = 6;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
