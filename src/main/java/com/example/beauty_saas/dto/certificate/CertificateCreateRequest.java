package com.example.beauty_saas.dto.certificate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CertificateCreateRequest {
    @NotNull(message = "Successful student ID cannot be null")
    private UUID successfulStudentId;

    @NotNull(message = "Course ID cannot be null")
    private UUID courseId;

    @NotBlank(message = "Certificate title cannot be blank")
    @Size(min = 5, max = 200, message = "Certificate title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Certificate number cannot be blank")
    @Size(max = 50, message = "Certificate number cannot exceed 50 characters")
    private String certificateNumber;

    @NotNull(message = "Issue date cannot be null")
    private LocalDate issueDate;
}
