package com.example.beautysaas.dto.successfulstudent;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SuccessfulStudentDto {
    private UUID id;
    private UUID parlourId;
    private String name;
    private UUID courseId;
    private String courseName;
    private LocalDate completionDate;
    private String testimonial;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
