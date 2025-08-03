package com.example.beautysaas.dto.certificate;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CertificateDto {
    private UUID id;
    private UUID parlourId;
    private String title;
    private String description;
    private String issuedBy;
    private LocalDate issueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
