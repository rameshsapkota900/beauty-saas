package com.example.beauty_saas.dto.successfulstudent;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SuccessfulStudentDto {
    private UUID id;
    private UUID parlourId;
    private String parlourName;
    private String studentName;
    private UUID courseId;
    private String courseName;
    private LocalDate completionDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
