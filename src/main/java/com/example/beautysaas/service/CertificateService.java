package com.example.beautysaas.service;

import com.example.beautysaas.dto.certificate.CertificateCreateRequest;
import com.example.beautysaas.dto.certificate.CertificateDto;
import com.example.beautysaas.dto.certificate.CertificateUpdateRequest;
import com.example.beautysaas.entity.Certificate;
import com.example.beautysaas.entity.Parlour;
import com.example.beautysaas.entity.User;
import com.example.beautysaas.exception.BeautySaasApiException;
import com.example.beautysaas.exception.ResourceNotFoundException;
import com.example.beautysaas.repository.CertificateRepository;
import com.example.beautysaas.repository.ParlourRepository;
import com.example.beautysaas.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final ParlourRepository parlourRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CertificateService(CertificateRepository certificateRepository, ParlourRepository parlourRepository, UserRepository userRepository, ModelMapper modelMapper) \{
        this.certificateRepository = certificateRepository;
        this.parlourRepository = parlourRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public CertificateDto addCertificate(String adminEmail, UUID parlourId, CertificateCreateRequest createRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not associated with this parlour.");
        }

        Certificate certificate = Certificate.builder()
                .parlour(parlour)
                .title(createRequest.getTitle())
                .description(createRequest.getDescription())
                .issuedBy(createRequest.getIssuedBy())
                .issueDate(createRequest.getIssueDate())
                .build();

        Certificate savedCertificate = certificateRepository.save(certificate);
        log.info("Certificate added: {}", savedCertificate.getId());
        return modelMapper.map(savedCertificate, CertificateDto.class);
    }

    public Page<CertificateDto> listCertificates(UUID parlourId, Pageable pageable) {
        Page<Certificate> certificates = certificateRepository.findByParlourId(parlourId, pageable);
        return certificates.map(certificate -> modelMapper.map(certificate, CertificateDto.class));
    }

    public CertificateDto getCertificateDetail(UUID id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate", "id", id));
        return modelMapper.map(certificate, CertificateDto.class);
    }

    @Transactional
    public CertificateDto updateCertificate(String adminEmail, UUID id, CertificateUpdateRequest updateRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate", "id", id));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(certificate.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to update this certificate.");
        }

        modelMapper.map(updateRequest, certificate); // Map non-null fields from DTO to entity

        Certificate updatedCertificate = certificateRepository.save(certificate);
        log.info("Certificate updated: {}", updatedCertificate.getId());
        return modelMapper.map(updatedCertificate, CertificateDto.class);
    }

    @Transactional
    public void deleteCertificate(String adminEmail, UUID id) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate", "id", id));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(certificate.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to delete this certificate.");
        }

        certificateRepository.delete(certificate);
        log.info("Certificate deleted: {}", id);
    }
}
