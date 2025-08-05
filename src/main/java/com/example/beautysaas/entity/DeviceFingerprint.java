package com.example.beautysaas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device_fingerprints")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceFingerprint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String fingerprint;
    
    @Column(name = "first_seen_ip")
    private String firstSeenIp;
    
    @Column(name = "last_seen_ip")
    private String lastSeenIp;
    
    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;
    
    @Column(name = "usage_count")
    @Builder.Default
    private Integer usageCount = 0;
    
    @Column(name = "trust_score")
    @Builder.Default
    private Double trustScore = 0.0;
}
