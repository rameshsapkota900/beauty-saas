package com.example.beauty_saas.service;

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

    public ParlourService(ParlourRepository parlourRepository, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.parlourRepository = parlourRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ParlourDto createParlour(ParlourCreateRequest createRequest) {
        if (parlourRepository.existsBySlug(createRequest.getSlug())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Parlour with slug '" + createRequest.getSlug() + "' already exists.");
        }

        Parlour parlour = modelMapper.map(createRequest, Parlour.class);
        Parlour savedParlour = parlourRepository.save(parlour);

        // Automatically create an Admin user for the new parlour
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ADMIN"));

        User adminUser = User.builder()
                .name(savedParlour.getName() + " Admin")
                .email("admin@" + savedParlour.getSlug() + ".com") // Example admin email
                .password(passwordEncoder.encode("password")) // Default password, should be changed
                .role(adminRole)
                .parlour(savedParlour)
                .build();
        userRepository.save(adminUser);
        log.info("Admin user created for new parlour {}: {}", savedParlour.getName(), adminUser.getEmail());

        log.info("Parlour created: {}", savedParlour.getId());
        return modelMapper.map(savedParlour, ParlourDto.class);
    }

    public ParlourDto getParlourById(UUID parlourId) {
        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));
        log.debug("Fetching parlour: {}", parlourId);
        return modelMapper.map(parlour, ParlourDto.class);
    }

    public Page<ParlourDto> getAllParlours(Pageable pageable) {
        log.debug("Fetching all parlours.");
        Page<Parlour> parlours = parlourRepository.findAll(pageable);
        return parlours.map(parlour -> modelMapper.map(parlour, ParlourDto.class));
    }

    @Transactional
    public ParlourDto updateParlour(UUID parlourId, ParlourUpdateRequest updateRequest) {
        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));

        if (updateRequest.getSlug() != null && !updateRequest.getSlug().equalsIgnoreCase(parlour.getSlug())) {
            if (parlourRepository.existsBySlug(updateRequest.getSlug())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Parlour with slug '" + updateRequest.getSlug() + "' already exists.");
            }
            parlour.setSlug(updateRequest.getSlug());
        }
        if (updateRequest.getName() != null) {
            parlour.setName(updateRequest.getName());
        }
        if (updateRequest.getAddress() != null) {
            parlour.setAddress(updateRequest.getAddress());
        }
        if (updateRequest.getPhoneNumber() != null) {
            parlour.setPhoneNumber(updateRequest.getPhoneNumber());
        }
        if (updateRequest.getEmail() != null) {
            parlour.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getDescription() != null) {
            parlour.setDescription(updateRequest.getDescription());
        }

        Parlour updatedParlour = parlourRepository.save(parlour);
        log.info("Parlour updated: {}", updatedParlour.getId());
        return modelMapper.map(updatedParlour, ParlourDto.class);
    }

    @Transactional
    public void deleteParlour(UUID parlourId) {
        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));

        // Due to CascadeType.ALL and orphanRemoval=true on @OneToMany relationships in Parlour entity,
        // deleting the parlour will automatically delete all associated users (admins/customers tied to this parlour),
        // staff, categories, services, courses, products, bookings, successful students, and certificates.
        parlourRepository.delete(parlour);
        log.info("Parlour and all associated data deleted: {}", parlourId);
    }
}
