package com.beautyparlour.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Entity
@Table(name = "certificates")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "parlour_id", nullable = false)
    private UUID parlourId;

    @NotBlank
    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "image_url")
    private String imageUrl;

    // Constructors
    public Certificate() {}

    public Certificate(UUID parlourId, String studentName, String imageUrl) {
        this.parlourId = parlourId;
        this.studentName = studentName;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getParlourId() { return parlourId; }
    public void setParlourId(UUID parlourId) { this.parlourId = parlourId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
