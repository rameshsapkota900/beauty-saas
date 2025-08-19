package com.beautyparlour.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Entity
@Table(name = "successful_students")
public class SuccessfulStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "parlour_id", nullable = false)
    private UUID parlourId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    // Constructors
    public SuccessfulStudent() {}

    public SuccessfulStudent(UUID parlourId, String name, String imageUrl) {
        this.parlourId = parlourId;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getParlourId() { return parlourId; }
    public void setParlourId(UUID parlourId) { this.parlourId = parlourId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
