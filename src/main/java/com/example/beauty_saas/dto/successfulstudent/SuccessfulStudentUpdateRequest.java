package com.example.beauty_saas.dto.successfulstudent;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class SuccessfulStudentUpdateRequest {
    @Size(min = 2, max = 100, message = "Student name must be between 2 and 100 characters")
    private String studentName;

    private UUID courseId;

    private LocalDate completionDate;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
