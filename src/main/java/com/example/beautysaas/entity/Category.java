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
@org.hibernate.annotations.Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "bs_categories", 
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"parlour_id", "name"}),
        @UniqueConstraint(columnNames = {"parlour_id", "slug"})
    },
    indexes = {
        @Index(name = "idx_category_parlour_id", columnList = "parlour_id"),
        @Index(name = "idx_category_parent_id", columnList = "parent_id"),
        @Index(name = "idx_category_slug", columnList = "slug"),
        @Index(name = "idx_category_active", columnList = "active"),
        @Index(name = "idx_category_display_order", columnList = "display_order"),
        @Index(name = "idx_category_path", columnList = "path")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parlour_id", nullable = false)
    private Parlour parlour;

    @Column(nullable = false, length = 100)
    @jakarta.validation.constraints.NotBlank(message = "Category name is required")
    @jakarta.validation.constraints.Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    @Column(length = 500)
    @jakarta.validation.constraints.Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "display_order")
    private int displayOrder;

    @Column(name = "icon_url")
    @jakarta.validation.constraints.Pattern(regexp = "^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$", message = "Invalid URL format")
    private String iconUrl;

    @Column(name = "color_code", length = 7)
    @jakarta.validation.constraints.Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Invalid color code format")
    private String colorCode;

    @Column(name = "meta_keywords", length = 255)
    @jakarta.validation.constraints.Size(max = 255, message = "Meta keywords cannot exceed 255 characters")
    private String metaKeywords;

    @Column(name = "meta_description", length = 500)
    @jakarta.validation.constraints.Size(max = 500, message = "Meta description cannot exceed 500 characters")
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
    @org.hibernate.annotations.Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE)
    @org.hibernate.annotations.BatchSize(size = 20)
    @OrderBy("displayOrder ASC")
    private List<Category> children;
    
    @Column(name = "level")
    @Builder.Default
    private Integer level = 0;
    
    @Column(name = "path")
    private String path;
    
    @PrePersist
    @PreUpdate
    private void updatePathAndLevel() {
        // Handle level updates
        if (parent != null) {
            this.level = (parent.getLevel() != null) ? parent.getLevel() + 1 : 1;
        } else {
            this.level = 0;
        }

        // Handle path updates with null safety
        if (this.id != null) {
            if (parent != null && parent.getPath() != null) {
                this.path = parent.getPath() + "/" + this.id;
            } else {
                this.path = "/" + this.id;
            }
        }
        
        // Ensure slug is always set
        if (this.slug == null || this.slug.trim().isEmpty()) {
            this.slug = generateSlug();
        }

        // Ensure display order is valid
        if (this.displayOrder < 0) {
            this.displayOrder = 0;
        }

        // Clean up meta fields
        if (this.metaKeywords != null) {
            this.metaKeywords = this.metaKeywords.trim();
        }
        if (this.metaDescription != null) {
            this.metaDescription = this.metaDescription.trim();
        }
        if (this.name != null) {
            this.name = this.name.trim();
        }
    }
    
    private String generateSlug() {
        return SlugUtil.toSlug(this.name);
    }

    @PrePersist
    @PreUpdate
    private void validateBusinessRules() {
        // Check for circular references
        if (this.parent != null && (this.equals(this.parent) || this.parent.isDescendantOf(this))) {
            throw new CategoryValidationException("Circular reference detected in category hierarchy");
        }

        // Validate level constraints
        if (this.level != null && this.level < 0) {
            throw new CategoryValidationException("Category level cannot be negative");
        }

        // Validate max depth (assuming max depth of 5)
        if (this.level != null && this.level > 5) {
            throw new CategoryValidationException("Category hierarchy cannot exceed 5 levels");
        }

        // Validate parent-child relationship
        if (this.parent != null && !this.parlour.equals(this.parent.getParlour())) {
            throw new CategoryValidationException("Parent category must belong to the same parlour");
        }

        // Validate path format
        if (this.path != null && !this.path.startsWith("/")) {
            throw new CategoryValidationException("Category path must start with '/'");
        }
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public boolean isDescendantOf(Category ancestor) {
        if (this.parent == null) return false;
        if (this.parent.equals(ancestor)) return true;
        return this.parent.isDescendantOf(ancestor);
    }

    public boolean isAncestorOf(Category descendant) {
        return descendant != null && descendant.isDescendantOf(this);
    }

    public int getDepth() {
        return this.level != null ? this.level : 0;
    }

    public String getFullPath() {
        if (this.path == null) {
            updatePathAndLevel();
        }
        return this.path;
    }

    public boolean isLeaf() {
        return !hasChildren();
    }
}
