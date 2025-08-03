package com.example.beautysaas.service;

import com.example.beautysaas.dto.successfulstudent.SuccessfulStudentCreateRequest;
import com.example.beautysaas.dto.successfulstudent.SuccessfulStudentDto;
import com.example.beautysaas.dto.successfulstudent.SuccessfulStudentUpdateRequest;
import com.example.beautysaas.entity.Course;
import com.example.beautysaas.entity.Parlour;
import com.example.beautysaas.entity.SuccessfulStudent;
import com.example.beautysaas.entity.User;
import com.example.beautysaas.exception.BeautySaasApiException;
import com.example.beautysaas.exception.ResourceNotFoundException;
import com.example.beautysaas.repository.CourseRepository;
import com.example.beautysaas.repository.ParlourRepository;
import com.example.beautysaas.repository.SuccessfulStudentRepository;
import com.example.beautysaas.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class SuccessfulStudentService {

    private final SuccessfulStudentRepository successfulStudentRepository;
    private final ParlourRepository parlourRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public SuccessfulStudentService(SuccessfulStudentRepository successfulStudentRepository, ParlourRepository parlourRepository, CourseRepository courseRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.successfulStudentRepository = successfulStudentRepository;
        this.parlourRepository = parlourRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public SuccessfulStudentDto addSuccessfulStudent(String adminEmail, UUID parlourId, SuccessfulStudentCreateRequest createRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));
        Course course = courseRepository.findById(createRequest.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", createRequest.getCourseId()));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not associated with this parlour.");
        }
        if (!course.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Course does not belong to the specified parlour.");
        }

        SuccessfulStudent student = SuccessfulStudent.builder()
                .parlour(parlour)
                .name(createRequest.getName())
                .course(course)
                .completionDate(createRequest.getCompletionDate())
                .testimonial(createRequest.getTestimonial())
                .build();

        SuccessfulStudent savedStudent = successfulStudentRepository.save(student);
        log.info("Successful student added: {}", savedStudent.getId());
        return mapToDto(savedStudent);
    }

    public Page<SuccessfulStudentDto> listSuccessfulStudents(UUID parlourId, Pageable pageable) {
        Page<SuccessfulStudent> students = successfulStudentRepository.findByParlourId(parlourId, pageable);
        return students.map(this::mapToDto);
    }

    public SuccessfulStudentDto getStudentDetail(UUID id) {
        SuccessfulStudent student = successfulStudentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SuccessfulStudent", "id", id));
        return mapToDto(student);
    }

    @Transactional
    public SuccessfulStudentDto updateSuccessfulStudent(String adminEmail, UUID id, SuccessfulStudentUpdateRequest updateRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        SuccessfulStudent student = successfulStudentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SuccessfulStudent", "id", id));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(student.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to update this student.");
        }

        if (updateRequest.getName() != null) {
            student.setName(updateRequest.getName());
        }
        if (updateRequest.getCourseId() != null) {
            Course course = courseRepository.findById(updateRequest.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course", "id", updateRequest.getCourseId()));
            if (!course.getParlour().getId().equals(student.getParlour().getId())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "New course does not belong to the same parlour.");
            }
            student.setCourse(course);
        }
        if (updateRequest.getCompletionDate() != null) {
            student.setCompletionDate(updateRequest.getCompletionDate());
        }
        if (updateRequest.getTestimonial() != null) {
            student.setTestimonial(updateRequest.getTestimonial());
        }

        SuccessfulStudent updatedStudent = successfulStudentRepository.save(student);
        log.info("Successful student updated: {}", updatedStudent.getId());
        return mapToDto(updatedStudent);
    }

    @Transactional
    public void deleteSuccessfulStudent(String adminEmail, UUID id) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        SuccessfulStudent student = successfulStudentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SuccessfulStudent", "id", id));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(student.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to delete this student.");
        }

        successfulStudentRepository.delete(student);
        log.info("Successful student deleted: {}", id);
    }

    private SuccessfulStudentDto mapToDto(SuccessfulStudent student) {
        SuccessfulStudentDto dto = modelMapper.map(student, SuccessfulStudentDto.class);
        dto.setParlourId(student.getParlour().getId());
        dto.setCourseName(student.getCourse().getName());
        return dto;
    }
}
