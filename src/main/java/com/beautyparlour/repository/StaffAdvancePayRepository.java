package com.beautyparlour.repository;

import com.beautyparlour.entity.StaffAdvancePay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface StaffAdvancePayRepository extends JpaRepository<StaffAdvancePay, UUID> {
    List<StaffAdvancePay> findByStaffId(UUID staffId);
    
    @Query("SELECT COALESCE(SUM(sap.amount), 0) FROM StaffAdvancePay sap WHERE sap.staffId = :staffId")
    BigDecimal getTotalAdvanceByStaffId(@Param("staffId") UUID staffId);
}
