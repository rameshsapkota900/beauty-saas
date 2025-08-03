package com.example.beautysaas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "parlours", uniqueConstraints = {
        @UniqueConstraint(columnNames = "slug")
})
@EntityListeners(AuditingEntityListener.class)
public class Parlour {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String slug;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_user_id", referencedColumnName = "id")
    private User adminUser; // The admin user associated with this parlour

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
