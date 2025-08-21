package com.beautyparlour.repository;

import com.beautyparlour.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {
    List<Staff> findByParlourId(UUID parlourId);
    
    Optional<Staff> findByIdAndParlourId(UUID id, UUID parlourId);
    
    @Query("SELECT s FROM Staff s WHERE s.parlourId = :parlourId AND s.designation = :designation")
    List<Staff> findByParlourIdAndDesignation(@Param("parlourId") UUID parlourId, 
                                              @Param("designation") String designation);
    
    @Query("SELECT s FROM Staff s WHERE s.parlourId = :parlourId AND s.baseSalary >= :minSalary")
    List<Staff> findByParlourIdAndBaseSalaryGreaterThanEqual(@Param("parlourId") UUID parlourId, 
                                                             @Param("minSalary") BigDecimal minSalary);
    
    @Query("SELECT COUNT(s) FROM Staff s WHERE s.parlourId = :parlourId")
    long countByParlourId(@Param("parlourId") UUID parlourId);
}
