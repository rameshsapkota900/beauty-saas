package com.beautyparlour.service;

import com.beautyparlour.dto.request.CreateParlourRequest;
import com.beautyparlour.dto.response.ParlourResponse;
import com.beautyparlour.entity.Admin;
import com.beautyparlour.entity.Parlour;
import com.beautyparlour.exception.ResourceNotFoundException;
import com.beautyparlour.repository.AdminRepository;
import com.beautyparlour.repository.ParlourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SuperAdminService {

    @Autowired
    private ParlourRepository parlourRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public ParlourResponse createParlour(CreateParlourRequest request) {
        // Check if admin email already exists
        if (adminRepository.existsByEmail(request.getAdminEmail())) {
            throw new RuntimeException("Admin email already exists");
        }

        // Create parlour
        Parlour parlour = new Parlour(
                request.getParlourName(),
                request.getLogoUrl(),
                request.getContactInfo()
        );
        parlour = parlourRepository.save(parlour);

        // Create admin
        Admin admin = new Admin(
                parlour.getParlourId(),
                request.getAdminName(),
                request.getAdminEmail(),
                passwordEncoder.encode(request.getAdminPassword())
        );
        admin = adminRepository.save(admin);

        // Create response
        ParlourResponse response = new ParlourResponse();
        response.setParlourId(parlour.getParlourId());
        response.setName(parlour.getName());
        response.setLogoUrl(parlour.getLogoUrl());
        response.setContactInfo(parlour.getContactInfo());
        response.setCreatedAt(parlour.getCreatedAt());
        response.setAdminName(admin.getName());
        response.setAdminEmail(admin.getEmail());

        return response;
    }

    public List<ParlourResponse> getAllParlours() {
        List<Parlour> parlours = parlourRepository.findAll();
        return parlours.stream().map(parlour -> {
            ParlourResponse response = new ParlourResponse();
            response.setParlourId(parlour.getParlourId());
            response.setName(parlour.getName());
            response.setLogoUrl(parlour.getLogoUrl());
            response.setContactInfo(parlour.getContactInfo());
            response.setCreatedAt(parlour.getCreatedAt());

            // Get admin info
            adminRepository.findByParlourId(parlour.getParlourId())
                    .ifPresent(admin -> {
                        response.setAdminName(admin.getName());
                        response.setAdminEmail(admin.getEmail());
                    });

            return response;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deleteParlour(UUID parlourId) {
        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour not found"));

        // Delete admin first
        adminRepository.findByParlourId(parlourId)
                .ifPresent(adminRepository::delete);

        // Delete parlour
        parlourRepository.delete(parlour);
    }
}
