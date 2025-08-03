package com.example.beauty_saas.service;

import com.example.beautysaas.dto.certificate.CertificateCreateRequest;
import com.example.beautysaas.dto.certificate.CertificateDto;
import com.example.beautysaas.dto.certificate.CertificateUpdateRequest;
import com.example.beautysaas.entity.Certificate;
import com.example.beautysaas.entity.Course;
import com.example.beautysaas.entity.Parlour;
import com.example.beautysaas.entity.SuccessfulStudent;
import com.example.beautysaas.entity.User;
import com.example.beautysaas.exception.BeautySaasApiException;
import com.example.beautysaas.exception.ResourceNotFoundException;
import com.example.beautysaas.repository.CertificateRepository;
import com.example.beautysaas.repository.CourseRepository;
import com.example.beautysaas.repository.ParlourRepository;
import com.example.beautysaas.repository.SuccessfulStudentRepository;
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
    private final SuccessfulStudentRepository successfulStudentRepository;
    private final CourseRepository courseRepository;
    private final ParlourRepository parlourRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CertificateService(CertificateRepository certificateRepository, SuccessfulStudentRepository successfulStudentRepository, CourseRepository courseRepository, ParlourRepository parlourRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.certificateRepository = certificateRepository;
        this.successfulStudentRepository = successfulStudentRepository;
        this.courseRepository = courseRepository;
        this.parlourRepository = parlourRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public CertificateDto createCertificate(String adminEmail, UUID parlourId, CertificateCreateRequest createRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));
        SuccessfulStudent student = successfulStudentRepository.findById(createRequest.getSuccessfulStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("SuccessfulStudent", "id", createRequest.getSuccessfulStudentId()));
        Course course = courseRepository.findById(createRequest.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", createRequest.getCourseId()));

        if (!student.getParlour().getId().equals(parlourId) || !course.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Student or Course does not belong to the specified parlour.");
        }

        if (certificateRepository.existsByParlourIdAndCertificateNumber(parlourId, createRequest.getCertificateNumber())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Certificate with number '" + createRequest.getCertificateNumber() + "' already exists in this parlour.");
        }

        Certificate certificate = modelMapper.map(createRequest, Certificate.class);
        certificate.setParlour(parlour);
        certificate.setSuccessfulStudent(student);
        certificate.setCourse(course);

        Certificate savedCertificate = certificateRepository.save(certificate);
        log.info("Certificate created: {}", savedCertificate.getId());
        return mapToDto(savedCertificate);
    }

    public CertificateDto getCertificateById(String userEmail, UUID parlourId, UUID certificateId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate", "id", certificateId));

        if (!certificate.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Certificate does not belong to the specified parlour.");
        }

        // Ensure user is authorized to view this parlour's data
        if (user.getRole().getName().equals("ADMIN") && (user.getParlour() == null || !user.getParlour().getId().equals(parlourId))) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "Admin is not authorized for this parlour.");
        }
        // Customers can view any parlour's certificates for now, but this can be restricted if needed.

        log.debug("Fetching certificate: {}", certificateId);
        return mapToDto(certificate);
    }

    public Page<CertificateDto> getAllCertificates(String userEmail, UUID parlourId, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        // Ensure user is authorized to view this parlour's data
        if (user.getRole().getName().equals("ADMIN") && (user.getParlour() == null || !user.getParlour().getId().equals(parlourId))) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "Admin is not authorized for this parlour.");
        }
        // Customers can view any parlour's certificates for now, but this can be restricted if needed.

        log.debug("Fetching all certificates for parlour: {}", parlourId);
        Page<Certificate> certificates = certificateRepository.findByParlourId(parlourId, pageable);
        return certificates.map(this::mapToDto);
    }

    @Transactional
    public CertificateDto updateCertificate(String adminEmail, UUID parlourId, UUID certificateId, CertificateUpdateRequest updateRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate", "id", certificateId));

        if (!certificate.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Certificate does not belong to the specified parlour.");
        }

        if (updateRequest.getSuccessfulStudentId() != null) {
            SuccessfulStudent student = successfulStudentRepository.findById(updateRequest.getSuccessfulStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("SuccessfulStudent", "id", updateRequest.getSuccessfulStudentId()));
            if (!student.getParlour().getId().equals(parlourId)) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Student does not belong to the specified parlour.");
            }
            certificate.setSuccessfulStudent(student);
        }
        if (updateRequest.getCourseId() != null) {
            Course course = courseRepository.findById(updateRequest.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course", "id", updateRequest.getCourseId()));
            if (!course.getParlour().getId().equals(parlourId)) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Course does not belong to the specified parlour.");
            }
            certificate.setCourse(course);
        }
        if (updateRequest.getTitle() != null) {
            certificate.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getCertificateNumber() != null && !updateRequest.getCertificateNumber().equalsIgnoreCase(certificate.getCertificateNumber())) {
            if (certificateRepository.existsByParlourIdAndCertificateNumber(parlourId, updateRequest.getCertificateNumber())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Certificate with number '" + updateRequest.getCertificateNumber() + "' already exists in this parlour.");
            }
            certificate.setCertificateNumber(updateRequest.getCertificateNumber());
        }
        if (updateRequest.getIssueDate() != null) {
            certificate.setIssueDate(updateRequest.getIssueDate());
        }

        Certificate updatedCertificate = certificateRepository.save(certificate);
        log.info("Certificate updated: {}", updatedCertificate.getId());
        return mapToDto(updatedCertificate);
    }

    @Transactional
    public void deleteCertificate(String adminEmail, UUID parlourId, UUID certificateId) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate", "id", certificateId));

        if (!certificate.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Certificate does not belong to the specified parlour.");
        }

        certificateRepository.delete(certificate);
        log.info("Certificate deleted: {}", certificateId);
    }

    private CertificateDto mapToDto(Certificate certificate) {
        CertificateDto dto = modelMapper.map(certificate, CertificateDto.class);
        dto.setParlourId(certificate.getParlour().getId());
        dto.setStudentName(certificate.getSuccessfulStudent().getStudentName());
        dto.setCourseName(certificate.getCourse().getName());
        return dto;
    }
}
