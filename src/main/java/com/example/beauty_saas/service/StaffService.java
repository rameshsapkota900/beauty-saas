package com.example.beauty_saas.service;

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
import java.time.LocalTime;
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

    public StaffService(StaffRepository staffRepository, ParlourRepository parlourRepository, UserRepository userRepository, AdvancePaymentRepository advancePaymentRepository, SalaryLogRepository salaryLogRepository, ModelMapper modelMapper) {
        this.staffRepository = staffRepository;
        this.parlourRepository = parlourRepository;
        this.userRepository = userRepository;
        this.advancePaymentRepository = advancePaymentRepository;
        this.salaryLogRepository = salaryLogRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public StaffDto createStaff(String adminEmail, UUID parlourId, StaffCreateRequest createRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));

        if (staffRepository.existsByParlourIdAndEmail(parlourId, createRequest.getEmail())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Staff with email '" + createRequest.getEmail() + "' already exists in this parlour.");
        }

        validateStaffAvailabilityTimes(createRequest.getAvailableStartTime(), createRequest.getAvailableEndTime());

        Staff staff = modelMapper.map(createRequest, Staff.class);
        staff.setParlour(parlour);

        Staff savedStaff = staffRepository.save(staff);
        log.info("Staff created: {}", savedStaff.getId());
        return mapToDto(savedStaff);
    }

    public StaffDto getStaffById(String userEmail, UUID parlourId, UUID staffId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));

        if (!staff.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Staff does not belong to the specified parlour.");
        }

        // Ensure user is authorized to view this parlour's data
        if (user.getRole().getName().equals("ADMIN") && (user.getParlour() == null || !user.getParlour().getId().equals(parlourId))) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "Admin is not authorized for this parlour.");
        }
        // Customers can view any parlour's staff for now, but this can be restricted if needed.

        log.debug("Fetching staff: {}", staffId);
        return mapToDto(staff);
    }

    public Page<StaffDto> getAllStaff(String userEmail, UUID parlourId, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        // Ensure user is authorized to view this parlour's data
        if (user.getRole().getName().equals("ADMIN") && (user.getParlour() == null || !user.getParlour().getId().equals(parlourId))) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "Admin is not authorized for this parlour.");
        }
        // Customers can view any parlour's staff for now, but this can be restricted if needed.

        log.debug("Fetching all staff for parlour: {}", parlourId);
        Page<Staff> staff = staffRepository.findByParlourId(parlourId, pageable);
        return staff.map(this::mapToDto);
    }

    @Transactional
    public StaffDto updateStaff(String adminEmail, UUID parlourId, UUID staffId, StaffUpdateRequest updateRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));

        if (!staff.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Staff does not belong to the specified parlour.");
        }

        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equalsIgnoreCase(staff.getEmail())) {
            if (staffRepository.existsByParlourIdAndEmail(parlourId, updateRequest.getEmail())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Staff with email '" + updateRequest.getEmail() + "' already exists in this parlour.");
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
        if (updateRequest.getHourlyRate() != null) {
            staff.setHourlyRate(updateRequest.getHourlyRate());
        }
        if (updateRequest.getAvailableStartTime() != null && updateRequest.getAvailableEndTime() != null) {
            validateStaffAvailabilityTimes(updateRequest.getAvailableStartTime(), updateRequest.getAvailableEndTime());
            staff.setAvailableStartTime(updateRequest.getAvailableStartTime());
            staff.setAvailableEndTime(updateRequest.getAvailableEndTime());
        } else if (updateRequest.getAvailableStartTime() != null) {
            validateStaffAvailabilityTimes(updateRequest.getAvailableStartTime(), staff.getAvailableEndTime());
            staff.setAvailableStartTime(updateRequest.getAvailableStartTime());
        } else if (updateRequest.getAvailableEndTime() != null) {
            validateStaffAvailabilityTimes(staff.getAvailableStartTime(), updateRequest.getAvailableEndTime());
            staff.setAvailableEndTime(updateRequest.getAvailableEndTime());
        }

        Staff updatedStaff = staffRepository.save(staff);
        log.info("Staff updated: {}", updatedStaff.getId());
        return mapToDto(updatedStaff);
    }

    @Transactional
    public void deleteStaff(String adminEmail, UUID parlourId, UUID staffId) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));

        if (!staff.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Staff does not belong to the specified parlour.");
        }

        // TODO: Add logic to prevent deletion if bookings are still linked to this staff
        // For now, it will likely cause a foreign key constraint violation if not handled by DB cascade rules.

        staffRepository.delete(staff);
        log.info("Staff deleted: {}", staffId);
    }

    @Transactional
    public AdvancePaymentDto recordAdvancePayment(String adminEmail, UUID parlourId, UUID staffId, AdvancePaymentCreateRequest createRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));

        if (!staff.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Staff does not belong to the specified parlour.");
        }

        AdvancePayment advancePayment = modelMapper.map(createRequest, AdvancePayment.class);
        advancePayment.setStaff(staff);

        AdvancePayment savedAdvancePayment = advancePaymentRepository.save(advancePayment);
        log.info("Advance payment recorded for staff {}: {}", staffId, savedAdvancePayment.getId());
        return mapToDto(savedAdvancePayment);
    }

    public Page<AdvancePaymentDto> getStaffAdvancePayments(String adminEmail, UUID parlourId, UUID staffId, Pageable pageable) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));

        if (!staff.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Staff does not belong to the specified parlour.");
        }

        log.debug("Fetching advance payments for staff {}: {}", staffId);
        Page<AdvancePayment> advancePayments = advancePaymentRepository.findByStaffId(staffId, pageable);
        return advancePayments.map(this::mapToDto);
    }

    public Page<SalaryLogDto> getStaffSalaryLogs(String adminEmail, UUID parlourId, UUID staffId, Pageable pageable) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));

        if (!staff.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Staff does not belong to the specified parlour.");
        }

        log.debug("Fetching salary logs for staff {}:
