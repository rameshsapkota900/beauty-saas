package com.beautyparlour.repository;

import com.beautyparlour.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {
    List<Service> findByParlourId(UUID parlourId);
    Optional<Service> findByIdAndParlourId(UUID id, UUID parlourId);
    List<Service> findByCategoryIdAndParlourId(UUID categoryId, UUID parlourId);
}
