package com.beautyparlour.repository;

import com.beautyparlour.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {
    List<Staff> findByParlourId(UUID parlourId);
    Optional<Staff> findByIdAndParlourId(UUID id, UUID parlourId);
}
