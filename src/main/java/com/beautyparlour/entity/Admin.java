package com.beautyparlour.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Entity
@Table(name = "admins")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "parlour_id", nullable = false)
    private UUID parlourId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parlour_id", insertable = false, updatable = false)
    private Parlour parlour;

    // Constructors
    public Admin() {}

    public Admin(UUID parlourId, String name, String email, String password) {
        this.parlourId = parlourId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getParlourId() { return parlourId; }
    public void setParlourId(UUID parlourId) { this.parlourId = parlourId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Parlour getParlour() { return parlour; }
    public void setParlour(Parlour parlour) { this.parlour = parlour; }
}
