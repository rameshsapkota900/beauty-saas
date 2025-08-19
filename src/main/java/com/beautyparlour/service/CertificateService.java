package com.beautyparlour.service;

import com.beautyparlour.dto.request.CreateCertificateRequest;
import com.beautyparlour.entity.Certificate;
import com.beautyparlour.exception.ResourceNotFoundException;
import com.beautyparlour.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    public Certificate createCertificate(CreateCertificateRequest request, UUID parlourId) {
        Certificate certificate = new Certificate(
                parlourId,
                request.getStudentName(),
                request.getImageUrl()
        );
        return certificateRepository.save(certificate);
    }

    public List<Certificate> getCertificatesByParlour(UUID parlourId) {
        return certificateRepository.findByParlourId(parlourId);
    }

    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    public void deleteCertificate(UUID certificateId, UUID parlourId) {
        Certificate certificate = certificateRepository.findByIdAndParlourId(certificateId, parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        certificateRepository.delete(certificate);
    }
}
