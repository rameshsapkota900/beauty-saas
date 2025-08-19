package com.beautyparlour.repository;

import com.beautyparlour.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByParlourId(UUID parlourId);
    Optional<Category> findByIdAndParlourId(UUID id, UUID parlourId);
}
