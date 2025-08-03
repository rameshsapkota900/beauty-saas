package com.example.beautysaas.dto.certificate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CertificateCreateRequest {
    @NotBlank(message = "Title cannot be empty")
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    private String title;

    private String description;

    @NotBlank(message = "Issued by cannot be empty")
    private String issuedBy;

    @NotNull(message = "Issue date cannot be null")
    private LocalDate issueDate;
}
