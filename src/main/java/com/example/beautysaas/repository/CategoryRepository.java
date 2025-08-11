package com.example.beautysaas.repository;

import com.example.beautysaas.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
