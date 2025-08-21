package com.beautyparlour.service;

import com.beautyparlour.dto.request.CreateStaffRequest;
import com.beautyparlour.dto.request.StaffAdvancePayRequest;
import com.beautyparlour.entity.Staff;
import com.beautyparlour.entity.StaffAdvancePay;
import com.beautyparlour.entity.StaffSalaryLog;
import com.beautyparlour.exception.BusinessRuleViolationException;
import com.beautyparlour.exception.ResourceNotFoundException;
import com.beautyparlour.repository.StaffAdvancePayRepository;
import com.beautyparlour.repository.StaffRepository;
import com.beautyparlour.repository.StaffSalaryLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffAdvancePayRepository staffAdvancePayRepository;

    @Autowired
    private StaffSalaryLogRepository staffSalaryLogRepository;

    public Staff createStaff(CreateStaffRequest request, UUID parlourId) {
        Staff staff = new Staff(
                parlourId,
                request.getName(),
                request.getPhoto(),
                request.getDesignation(),
                request.getBaseSalary()
        );
        return staffRepository.save(staff);
    }

    public List<Staff> getStaffByParlour(UUID parlourId) {
        return staffRepository.findByParlourId(parlourId);
    }

    public void deleteStaff(UUID staffId, UUID parlourId) {
        Staff staff = staffRepository.findByIdAndParlourId(staffId, parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
        staffRepository.delete(staff);
    }

    public StaffAdvancePay recordAdvancePay(UUID staffId, StaffAdvancePayRequest request, UUID parlourId) {
        Staff staff = staffRepository.findByIdAndParlourId(staffId, parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        // Business rule: Advance cannot exceed 80% of base salary
        BigDecimal maxAdvanceAllowed = staff.getBaseSalary().multiply(new BigDecimal("0.80"));
        BigDecimal currentTotalAdvance = staffAdvancePayRepository.getTotalAdvanceByStaffId(staffId);
        BigDecimal newTotalAdvance = currentTotalAdvance.add(request.getAmount());

        if (newTotalAdvance.compareTo(maxAdvanceAllowed) > 0) {
            throw new BusinessRuleViolationException(
                String.format("Total advance cannot exceed 80%% of base salary. " +
                    "Current advance: %s, Requested: %s, Maximum allowed: %s", 
                    currentTotalAdvance, request.getAmount(), maxAdvanceAllowed));
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException("Advance amount must be positive");
        }

        StaffAdvancePay advancePay = new StaffAdvancePay(staffId, request.getAmount());
        return staffAdvancePayRepository.save(advancePay);
    }

    public StaffSalaryLog calculateAndLogSalary(UUID staffId, UUID parlourId) {
        Staff staff = staffRepository.findByIdAndParlourId(staffId, parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        BigDecimal totalAdvance = staffAdvancePayRepository.getTotalAdvanceByStaffId(staffId);
        BigDecimal netSalary = staff.getBaseSalary().subtract(totalAdvance);

        if (netSalary.compareTo(BigDecimal.ZERO) < 0) {
            netSalary = BigDecimal.ZERO;
        }

        StaffSalaryLog salaryLog = new StaffSalaryLog(staffId, netSalary);
        return staffSalaryLogRepository.save(salaryLog);
    }

    public List<StaffSalaryLog> getStaffSalaryLog(UUID staffId, UUID parlourId) {
        // Verify staff belongs to parlour
        staffRepository.findByIdAndParlourId(staffId, parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        return staffSalaryLogRepository.findByStaffIdOrderByPaidOnDesc(staffId);
    }
}
