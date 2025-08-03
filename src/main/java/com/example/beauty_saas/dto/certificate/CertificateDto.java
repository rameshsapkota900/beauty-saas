package com.example.beauty_saas.dto.certificate;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CertificateDto {
    private UUID id;
    private UUID parlourId;
    private UUID successfulStudentId;
    private String studentName;
    private UUID courseId;
    private String courseName;
    private String title;
    private String certificateNumber;
    private LocalDate issueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
