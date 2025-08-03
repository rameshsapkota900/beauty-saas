package com.example.beauty_saas.dto.certificate;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CertificateUpdateRequest {
    private UUID successfulStudentId;
    private UUID courseId;

    @Size(min = 5, max = 200, message = "Certificate title must be between 5 and 200 characters")
    private String title;

    @Size(max = 50, message = "Certificate number cannot exceed 50 characters")
    private String certificateNumber;

    private LocalDate issueDate;
}
