package com.example.beautysaas.service;

import com.example.beautysaas.config.SecurityConfig;
import com.example.beautysaas.dto.parlour.ParlourCreateRequest;
import com.example.beautysaas.dto.parlour.ParlourDto;
import com.example.beautysaas.dto.parlour.ParlourUpdateRequest;
import com.example.beautysaas.entity.Parlour;
import com.example.beautysaas.entity.Role;
import com.example.beautysaas.entity.User;
import com.example.beautysaas.exception.BeautySaasApiException;
import com.example.beautysaas.exception.ResourceNotFoundException;
import com.example.beautysaas.repository.ParlourRepository;
import com.example.beautysaas.repository.RoleRepository;
import com.example.beautysaas.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class ParlourService {

    private final ParlourRepository parlourRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public ParlourService(ParlourRepository parlourRepository,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          SecurityConfig securityConfig, // Inject SecurityConfig to get passwordEncoder bean
                          ModelMapper modelMapper) {
        this.parlourRepository = parlourRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = securityConfig.passwordEncoder(); // Get the PasswordEncoder bean
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ParlourDto createParlour(ParlourCreateRequest createRequest) {
        if (parlourRepository.existsBySlug(createRequest.getSlug())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Parlour with this slug already exists.");
        }
        if (userRepository.existsByEmail(createRequest.getAdminUser().getEmail())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Admin user email is already registered.");
        }

        // Create Admin Role
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ADMIN"));

        // Create Parlour entity first to get its ID for the Admin user
        Parlour parlour = Parlour.builder()
                .name(createRequest.getName())
                .slug(createRequest.getSlug())
                .address(createRequest.getAddress())
                .phoneNumber(createRequest.getPhoneNumber())
                .contactEmail(createRequest.getContactEmail())
                .build();
        Parlour savedParlour = parlourRepository.save(parlour);

        // Create Admin User
        User adminUser = User.builder()
                .name(createRequest.getAdminUser().getName())
                .email(createRequest.getAdminUser().getEmail())
                .password(passwordEncoder.encode(createRequest.getAdminUser().getPassword()))
                .role(adminRole)
                .parlour(savedParlour) // Associate admin with the newly created parlour
                .build();
        User savedAdminUser = userRepository.save(adminUser);

        // Link the admin user back to the parlour
        savedParlour.setAdminUser(savedAdminUser);
        parlourRepository.save(savedParlour); // Save again to update the adminUser reference

        log.info("Parlour {} created with Admin user {}.", savedParlour.getName(), savedAdminUser.getEmail());
        return mapToDto(savedParlour);
    }

    public Page<ParlourDto> listAllParlours(Pageable pageable) {
        Page<Parlour> parlours = parlourRepository.findAll(pageable);
        return parlours.map(this::mapToDto);
    }

    @Transactional
    public ParlourDto updateParlour(UUID id, ParlourUpdateRequest updateRequest) {
        Parlour parlour = parlourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", id));

        if (updateRequest.getName() != null) {
            parlour.setName(updateRequest.getName());
        }
        if (updateRequest.getSlug() != null && !updateRequest.getSlug().equalsIgnoreCase(parlour.getSlug())) {
            if (parlourRepository.existsBySlug(updateRequest.getSlug())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Parlour with this slug already exists.");
            }
            parlour.setSlug(updateRequest.getSlug());
        }
        if (updateRequest.getAddress() != null) {
            parlour.setAddress(updateRequest.getAddress());
        }
        if (updateRequest.getPhoneNumber() != null) {
            parlour.setPhoneNumber(updateRequest.getPhoneNumber());
        }
        if (updateRequest.getContactEmail() != null) {
            parlour.setContactEmail(updateRequest.getContactEmail());
        }

        Parlour updatedParlour = parlourRepository.save(parlour);
        log.info("Parlour {} updated.", updatedParlour.getId());
        return mapToDto(updatedParlour);
    }

    @Transactional
    public void deleteParlour(UUID id) {
        Parlour parlour = parlourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", id));

        // Delete associated admin user first to avoid foreign key constraint issues
        if (parlour.getAdminUser() != null) {
            userRepository.delete(parlour.getAdminUser());
            log.info("Associated admin user {} deleted for parlour {}.", parlour.getAdminUser().getEmail(), id);
        }

        // TODO: Implement cascade deletion or re-assignment for all parlour-specific entities
        // (Categories, Services, Courses, Products, Staff, Bookings, Certificates, SuccessfulStudents)
        // For a production system, you'd typically handle this with care, e.g., soft delete or archive.
        // For now, direct deletion will trigger cascade if configured in JPA, or throw errors if not.

        parlourRepository.delete(parlour);
        log.info("Parlour {} deleted.", id);
    }

    private ParlourDto mapToDto(Parlour parlour) {
        ParlourDto dto = modelMapper.map(parlour, ParlourDto.class);
        if (parlour.getAdminUser() != null) {
            dto.setAdminUserId(parlour.getAdminUser().getId());
            dto.setAdminUserName(parlour.getAdminUser().getName());
            dto.setAdminUserEmail(parlour.getAdminUser().getEmail());
        }
        return dto;
    }
}
