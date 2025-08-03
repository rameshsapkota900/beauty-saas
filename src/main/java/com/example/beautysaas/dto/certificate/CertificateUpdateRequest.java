package com.example.beautysaas.dto.certificate;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CertificateUpdateRequest {
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    private String title;

    private String description;
    private String issuedBy;
    private LocalDate issueDate;
}
