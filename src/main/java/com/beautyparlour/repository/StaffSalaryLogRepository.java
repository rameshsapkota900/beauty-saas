package com.beautyparlour.repository;

import com.beautyparlour.entity.StaffSalaryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffSalaryLogRepository extends JpaRepository<StaffSalaryLog, UUID> {
    List<StaffSalaryLog> findByStaffIdOrderByPaidOnDesc(UUID staffId);
}
