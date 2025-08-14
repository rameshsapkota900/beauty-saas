package com.example.beautysaas.repository;

import com.example.beautysaas.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Page<Category> findByParlourId(UUID parlourId, Pageable pageable);
    
    Optional<Category> findByParlourIdAndName(UUID parlourId, String name);
    
    boolean existsByParlourIdAndNameIgnoreCase(UUID parlourId, String name);
    
    @Query("SELECT c FROM Category c WHERE c.parlour.id = :parlourId AND c.parent IS NULL ORDER BY c.displayOrder")
    List<Category> findRootCategories(@Param("parlourId") UUID parlourId);
    
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId ORDER BY c.displayOrder")
    List<Category> findChildCategories(@Param("parentId") UUID parentId);
    
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.parent.id = :categoryId")
    boolean hasChildren(@Param("categoryId") UUID categoryId);

    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.parlour.id = :parlourId AND c.slug = :slug AND (:excludeCategoryId IS NULL OR c.id != :excludeCategoryId)")
    boolean existsBySlugAndParlourId(@Param("slug") String slug, @Param("parlourId") UUID parlourId, @Param("excludeCategoryId") UUID excludeCategoryId);
    
    @Query("SELECT c FROM Category c WHERE c.parlour.id = :parlourId AND c.path LIKE :pathPattern")
    List<Category> findByPathPattern(
        @Param("parlourId") UUID parlourId,
        @Param("pathPattern") String pathPattern
    );
    
    @Query("SELECT c FROM Category c WHERE c.parlour.id = :parlourId AND c.level = :level ORDER BY c.displayOrder")
    List<Category> findByLevel(
        @Param("parlourId") UUID parlourId,
        @Param("level") Integer level
    );
    
    @Query("SELECT MAX(c.level) FROM Category c WHERE c.parlour.id = :parlourId")
    Optional<Integer> findMaxLevel(@Param("parlourId") UUID parlourId);
    
    @Query("SELECT c FROM Category c WHERE c.parlour.id = :parlourId AND c.deleted = false AND c.active = true ORDER BY c.displayOrder")
    Page<Category> findActiveByParlourId(@Param("parlourId") UUID parlourId, Pageable pageable);
    
    @Query("SELECT c FROM Category c WHERE c.parlour.id = :parlourId AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "c.deleted = false ORDER BY c.displayOrder")
    Page<Category> searchCategories(
        @Param("parlourId") UUID parlourId,
        @Param("searchTerm") String searchTerm,
        Pageable pageable
    );
    
    @Query("SELECT MAX(c.displayOrder) FROM Category c WHERE c.parlour.id = :parlourId AND c.parent.id = :parentId")
    Optional<Integer> findMaxDisplayOrder(
        @Param("parlourId") UUID parlourId,
        @Param("parentId") UUID parentId
    );
    
    @Query("UPDATE Category c SET c.deleted = true, c.deletedAt = :deletedAt, c.deletedBy = :deletedBy WHERE c.id = :id OR c.path LIKE CONCAT('%/', :id, '/%')")
    int softDeleteWithChildren(
        @Param("id") UUID id,
        @Param("deletedAt") LocalDateTime deletedAt,
        @Param("deletedBy") String deletedBy
    );
    
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.parlour.id = :parlourId AND " +
           "c.parent.id = :parentId AND c.displayOrder = :displayOrder AND " +
           "c.id != :excludeCategoryId AND c.deleted = false")
    boolean existsByDisplayOrder(
        @Param("parlourId") UUID parlourId,
        @Param("parentId") UUID parentId,
        @Param("displayOrder") int displayOrder,
        @Param("excludeCategoryId") UUID excludeCategoryId
    );

    @Query("SELECT c FROM Category c WHERE c.parlour.id = :parlourId AND c.deleted = :deleted AND c.active = :active ORDER BY c.displayOrder")
    Page<Category> findByStatus(
        @Param("parlourId") UUID parlourId,
        @Param("deleted") boolean deleted,
        @Param("active") boolean active,
        Pageable pageable
    );

    @Query("SELECT c FROM Category c WHERE c.parlour.id = :parlourId AND " +
           "(:metaKeywords IS NULL OR c.metaKeywords LIKE CONCAT('%', :metaKeywords, '%')) AND " +
           "(:colorCode IS NULL OR c.colorCode = :colorCode) AND " +
           "c.deleted = false ORDER BY c.displayOrder")
    Page<Category> findByMetadata(
        @Param("parlourId") UUID parlourId,
        @Param("metaKeywords") String metaKeywords,
        @Param("colorCode") String colorCode,
        Pageable pageable
    );

    @Query("SELECT c FROM Category c WHERE c.parlour.id = :parlourId AND " +
           "LENGTH(c.path) - LENGTH(REPLACE(c.path, '/', '')) = :depth AND " +
           "c.deleted = false ORDER BY c.path, c.displayOrder")
    List<Category> findByPathDepth(
        @Param("parlourId") UUID parlourId,
        @Param("depth") int depth
    );

    @Query("SELECT c FROM Category c WHERE c.parlour.id = :parlourId AND " +
           "c.parent.id = :parentId AND c.displayOrder >= :startOrder AND c.displayOrder <= :endOrder AND " +
           "c.deleted = false ORDER BY c.displayOrder")
    List<Category> findForReordering(
        @Param("parlourId") UUID parlourId,
        @Param("parentId") UUID parentId,
        @Param("startOrder") int startOrder,
        @Param("endOrder") int endOrder
    );
}
