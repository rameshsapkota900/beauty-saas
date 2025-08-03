package com.example.beautysaas.dto.parlour;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ParlourUpdateRequest {
    @Size(min = 2, max = 100, message = "Parlour name must be between 2 and 100 characters")
    private String name;

    @Size(min = 2, max = 50, message = "Parlour slug must be between 2 and 50 characters")
    private String slug;

    private String address;
    private String phoneNumber;

    @Email(message = "Contact email should be valid")
    private String contactEmail;
}
