package com.example.beautysaas.service;

import com.example.beautysaas.dto.advancepayment.AdvancePaymentCreateRequest;
import com.example.beautysaas.dto.advancepayment.AdvancePaymentDto;
import com.example.beautysaas.dto.salarylog.SalaryLogDto;
import com.example.beautysaas.dto.staff.StaffCreateRequest;
import com.example.beautysaas.dto.staff.StaffDto;
import com.example.beautysaas.dto.staff.StaffUpdateRequest;
import com.example.beautysaas.entity.AdvancePayment;
import com.example.beautysaas.entity.Parlour;
import com.example.beautysaas.entity.SalaryLog;
import com.example.beautysaas.entity.Staff;
import com.example.beautysaas.entity.User;
import com.example.beautysaas.exception.BeautySaasApiException;
import com.example.beautysaas.exception.ResourceNotFoundException;
import com.example.beautysaas.repository.AdvancePaymentRepository;
import com.example.beautysaas.repository.ParlourRepository;
import com.example.beautysaas.repository.SalaryLogRepository;
import com.example.beautysaas.repository.StaffRepository;
import com.example.beautysaas.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

@Service
@Slf4j
public class StaffService {

    private final StaffRepository staffRepository;
    private final ParlourRepository parlourRepository;
    private final UserRepository userRepository;
    private final AdvancePaymentRepository advancePaymentRepository;
    private final SalaryLogRepository salaryLogRepository;
    private final ModelMapper modelMapper;

    public StaffService(StaffRepository staffRepository,
                        ParlourRepository parlourRepository,
                        UserRepository userRepository,
                        AdvancePaymentRepository advancePaymentRepository,
                        SalaryLogRepository salaryLogRepository,
                        ModelMapper modelMapper) {
        this.staffRepository = staffRepository;
        this.parlourRepository = parlourRepository;
        this.userRepository = userRepository;
        this.advancePaymentRepository = advancePaymentRepository;
        this.salaryLogRepository = salaryLogRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public StaffDto addStaff(String adminEmail, UUID parlourId, StaffCreateRequest createRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not associated with this parlour.");
        }
        if (staffRepository.existsByParlourIdAndEmail(parlourId, createRequest.getEmail())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Staff with this email already exists for this parlour.");
        }
        if (createRequest.getAvailableStartTime().isAfter(createRequest.getAvailableEndTime()) || createRequest.getAvailableStartTime().equals(createRequest.getAvailableEndTime())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Available end time must be after start time.");
        }

        Staff staff = Staff.builder()
                .parlour(parlour)
                .name(createRequest.getName())
                .email(createRequest.getEmail())
                .phoneNumber(createRequest.getPhoneNumber())
                .specialization(createRequest.getSpecialization())
                .bio(createRequest.getBio())
                .baseSalary(createRequest.getBaseSalary())
                .isActive(createRequest.getIsActive())
                .availableStartTime(createRequest.getAvailableStartTime())
                .availableEndTime(createRequest.getAvailableEndTime())
                .build();

        Staff savedStaff = staffRepository.save(staff);
        log.info("Staff added: {}", savedStaff.getId());
        return modelMapper.map(savedStaff, StaffDto.class);
    }

    public Page<StaffDto> listStaff(String adminEmail, UUID parlourId, Pageable pageable) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not associated with this parlour.");
        }

        Page<Staff> staffPage = staffRepository.findByParlourId(parlourId, pageable);
        return staffPage.map(staff -> modelMapper.map(staff, StaffDto.class));
    }

    public StaffDto getStaffDetail(String adminEmail, UUID id) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(staff.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to view this staff.");
        }
        return modelMapper.map(staff, StaffDto.class);
    }

    @Transactional
    public StaffDto updateStaff(String adminEmail, UUID id, StaffUpdateRequest updateRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(staff.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to update this staff.");
        }

        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equalsIgnoreCase(staff.getEmail())) {
            if (staffRepository.existsByParlourIdAndEmail(staff.getParlour().getId(), updateRequest.getEmail())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Staff with this email already exists for this parlour.");
            }
            staff.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getName() != null) {
            staff.setName(updateRequest.getName());
        }
        if (updateRequest.getPhoneNumber() != null) {
            staff.setPhoneNumber(updateRequest.getPhoneNumber());
        }
        if (updateRequest.getSpecialization() != null) {
            staff.setSpecialization(updateRequest.getSpecialization());
        }
        if (updateRequest.getBio() != null) {
            staff.setBio(updateRequest.getBio());
        }
        if (updateRequest.getBaseSalary() != null) {
            staff.setBaseSalary(updateRequest.getBaseSalary());
        }
        if (updateRequest.getIsActive() != null) {
            staff.setIsActive(updateRequest.getIsActive());
        }
        if (updateRequest.getAvailableStartTime() != null) {
            staff.setAvailableStartTime(updateRequest.getAvailableStartTime());
        }
        if (updateRequest.getAvailableEndTime() != null) {
            staff.setAvailableEndTime(updateRequest.getAvailableEndTime());
        }
        if (staff.getAvailableStartTime() != null && staff.getAvailableEndTime() != null &&
                (staff.getAvailableStartTime().isAfter(staff.getAvailableEndTime()) || staff.getAvailableStartTime().equals(staff.getAvailableEndTime()))) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Available end time must be after start time.");
        }

        Staff updatedStaff = staffRepository.save(staff);
        log.info("Staff updated: {}", updatedStaff.getId());
        return modelMapper.map(updatedStaff, StaffDto.class);
    }

    @Transactional
    public void deleteStaff(String adminEmail, UUID id) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(staff.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to delete this staff.");
        }

        // TODO: Add logic to prevent deletion if bookings or salary logs are linked to this staff
        staffRepository.delete(staff);
        log.info("Staff deleted: {}", id);
    }

    @Transactional
    public AdvancePaymentDto addAdvancePayment(String adminEmail, UUID staffId, AdvancePaymentCreateRequest createRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(staff.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to add advance payment for this staff.");
        }

        AdvancePayment advancePayment = AdvancePayment.builder()
                .staff(staff)
                .amount(createRequest.getAmount())
                .paymentDate(createRequest.getPaymentDate() != null ? createRequest.getPaymentDate() : LocalDate.now())
                .notes(createRequest.getNotes())
                .build();

        AdvancePayment savedAdvancePayment = advancePaymentRepository.save(advancePayment);
        log.info("Advance payment added for staff {}: {}", staffId, savedAdvancePayment.getId());
        return modelMapper.map(savedAdvancePayment, AdvancePaymentDto.class);
    }

    @Transactional
    public SalaryLogDto calculateAndRecordSalary(String adminEmail, UUID staffId, int month, int year) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(staff.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to calculate salary for this staff.");
        }

        LocalDate periodMonth = YearMonth.of(year, month).atDay(1);

        // Check if salary for this month is already logged
        if (salaryLogRepository.findByStaffIdAndPeriodMonth(staffId, periodMonth).isPresent()) {
            throw new BeautySaasApiException(HttpStatus.CONFLICT, "Salary for this staff and month has already been recorded.");
        }

        // Calculate total advance payments for the month
        LocalDate startDate = periodMonth;
        LocalDate endDate = periodMonth.lengthOfMonth();
        BigDecimal totalAdvancePayments = advancePaymentRepository.findByStaffIdAndPaymentDateBetween(staffId, startDate, endDate)
                .stream()
                .map(AdvancePayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netSalaryPaid = staff.getBaseSalary().subtract(totalAdvancePayments);
        if (netSalaryPaid.compareTo(BigDecimal.ZERO) < 0) {
            netSalaryPaid = BigDecimal.ZERO; // Ensure net salary is not negative
            log.warn("Net salary for staff {} for {}-{} is negative. Setting to zero. Total advance payments: {}", staffId, month, year, totalAdvancePayments);
        }

        SalaryLog salaryLog = SalaryLog.builder()
                .staff(staff)
                .baseSalary(staff.getBaseSalary())
                .totalAdvancePayments(totalAdvancePayments)
                .netSalaryPaid(netSalaryPaid)
                .periodMonth(periodMonth)
                .build();

        SalaryLog savedSalaryLog = salaryLogRepository.save(salaryLog);
        log.info("Salary logged for staff {} for {}-{}: Net Salary {}", staffId, month, year, netSalaryPaid);
        return modelMapper.map(savedSalaryLog, SalaryLogDto.class);
    }

    public Page<SalaryLogDto> getSalaryLogs(String adminEmail, UUID staffId, Pageable pageable) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(staff.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to view salary logs for this staff.");
        }

        Page<SalaryLog> salaryLogs = salaryLogRepository.findByStaffId(staffId, pageable);
        return salaryLogs.map(this::mapToDto);
    }

    private SalaryLogDto mapToDto(SalaryLog salaryLog) {
        SalaryLogDto dto = modelMapper.map(salaryLog, SalaryLogDto.class);
        dto.setStaffName(salaryLog.getStaff().getName());
        return dto;
    }
}
