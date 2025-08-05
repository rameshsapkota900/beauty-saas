package com.example.beautysaas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {
    
    @Id
    private String sessionId;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "is_active")
    private boolean isActive;
    
    @Column(name = "revocation_reason")
    private String revocationReason;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "geolocation_id")
    private GeoLocation geoLocation;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastActivity = createdAt;
        isActive = true;
    }
    
    public void markAsInactive(String reason) {
        this.isActive = false;
        this.revocationReason = reason;
    }
    
    public void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }
}
