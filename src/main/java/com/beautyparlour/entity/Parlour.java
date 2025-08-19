package com.beautyparlour.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "parlours")
public class Parlour {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "parlour_id")
    private UUID parlourId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "contact_info")
    private String contactInfo;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public Parlour() {}

    public Parlour(String name, String logoUrl, String contactInfo) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.contactInfo = contactInfo;
    }

    // Getters and Setters
    public UUID getParlourId() { return parlourId; }
    public void setParlourId(UUID parlourId) { this.parlourId = parlourId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
