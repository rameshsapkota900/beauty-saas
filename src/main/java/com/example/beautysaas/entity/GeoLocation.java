package com.example.beautysaas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "geolocation_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoLocation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String ipAddress;
    
    @Column
    private String country;
    
    @Column
    private String city;
    
    @Column
    private String region;
    
    @Column(precision = 10, scale = 6)
    private Double latitude;
    
    @Column(precision = 10, scale = 6)
    private Double longitude;
    
    @Column
    private String timezone;
    
    @Column
    private String isp;
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    @Column(name = "last_access_email")
    private String lastAccessEmail;
}
