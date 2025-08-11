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
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"parlour_id", "name"})
})
@EntityListeners(AuditingEntityListener.class)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parlour_id", nullable = false)
    private Parlour parlour;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "display_order")
    private int displayOrder;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "color_code", length = 7)
    private String colorCode;

    @Column(name = "meta_keywords")
    private String metaKeywords;

    @Column(name = "meta_description")
    private String metaDescription;
    
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children;
    
    @Column(name = "level")
    @Builder.Default
    private Integer level = 0;
    
    @Column(name = "path")
    private String path;
    
    @PrePersist
    @PreUpdate
    private void updatePathAndLevel() {
        if (parent != null) {
            this.level = parent.getLevel() + 1;
            this.path = parent.getPath() + "/" + this.id;
        } else {
            this.level = 0;
            this.path = "/" + this.id;
        }
        
        if (this.slug == null || this.slug.isEmpty()) {
            this.slug = generateSlug();
        }
    }
    
    private String generateSlug() {
        return SlugUtil.toSlug(this.name);
    }
}
