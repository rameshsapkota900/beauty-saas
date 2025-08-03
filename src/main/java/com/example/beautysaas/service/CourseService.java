package com.example.beautysaas.service;

import com.example.beautysaas.dto.course.CourseCreateRequest;
import com.example.beautysaas.dto.course.CourseDto;
import com.example.beautysaas.dto.course.CourseUpdateRequest;
import com.example.beautysaas.entity.Category;
import com.example.beautysaas.entity.Course;
import com.example.beautysaas.entity.Parlour;
import com.example.beautysaas.entity.User;
import com.example.beautysaas.exception.BeautySaasApiException;
import com.example.beautysaas.exception.ResourceNotFoundException;
import com.example.beautysaas.repository.CategoryRepository;
import com.example.beautysaas.repository.CourseRepository;
import com.example.beautysaas.repository.ParlourRepository;
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
public class CourseService {

    private final CourseRepository courseRepository;
    private final ParlourRepository parlourRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CourseService(CourseRepository courseRepository, ParlourRepository parlourRepository, CategoryRepository categoryRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.courseRepository = courseRepository;
        this.parlourRepository = parlourRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public CourseDto addCourse(String adminEmail, UUID parlourId, CourseCreateRequest createRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));
        Category category = categoryRepository.findById(createRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", createRequest.getCategoryId()));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not associated with this parlour.");
        }
        if (!category.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Category does not belong to the specified parlour.");
        }
        if (courseRepository.existsByParlourIdAndNameIgnoreCase(parlourId, createRequest.getName())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Course with this name already exists for this parlour.");
        }
        if (createRequest.getAvailableStartTime().isAfter(createRequest.getAvailableEndTime()) || createRequest.getAvailableStartTime().equals(createRequest.getAvailableEndTime())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Available end time must be after start time.");
        }

        Course course = Course.builder()
                .parlour(parlour)
                .name(createRequest.getName())
                .description(createRequest.getDescription())
                .price(createRequest.getPrice())
                .durationMinutes(createRequest.getDurationMinutes())
                .category(category)
                .isActive(createRequest.getIsActive())
                .availableStartTime(createRequest.getAvailableStartTime())
                .availableEndTime(createRequest.getAvailableEndTime())
                .build();

        Course savedCourse = courseRepository.save(course);
        log.info("Course added: {}", savedCourse.getId());
        return mapToDto(savedCourse);
    }

    public Page<CourseDto> listCourses(UUID parlourId, Pageable pageable) {
        Page<Course> courses = courseRepository.findByParlourId(parlourId, pageable);
        return courses.map(this::mapToDto);
    }

    public CourseDto getCourseDetail(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        return mapToDto(course);
    }

    @Transactional
    public CourseDto updateCourse(String adminEmail, UUID id, CourseUpdateRequest updateRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(course.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to update this course.");
        }

        if (updateRequest.getName() != null && !updateRequest.getName().equalsIgnoreCase(course.getName())) {
            if (courseRepository.existsByParlourIdAndNameIgnoreCase(course.getParlour().getId(), updateRequest.getName())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Course with this name already exists for this parlour.");
            }
            course.setName(updateRequest.getName());
        }
        if (updateRequest.getDescription() != null) {
            course.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getPrice() != null) {
            course.setPrice(updateRequest.getPrice());
        }
        if (updateRequest.getDurationMinutes() != null) {
            course.setDurationMinutes(updateRequest.getDurationMinutes());
        }
        if (updateRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", updateRequest.getCategoryId()));
            if (!category.getParlour().getId().equals(course.getParlour().getId())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "New category does not belong to the same parlour.");
            }
            course.setCategory(category);
        }
        if (updateRequest.getIsActive() != null) {
            course.setIsActive(updateRequest.getIsActive());
        }
        if (updateRequest.getAvailableStartTime() != null) {
            course.setAvailableStartTime(updateRequest.getAvailableStartTime());
        }
        if (updateRequest.getAvailableEndTime() != null) {
            course.setAvailableEndTime(updateRequest.getAvailableEndTime());
        }
        if (course.getAvailableStartTime() != null && course.getAvailableEndTime() != null &&
                (course.getAvailableStartTime().isAfter(course.getAvailableEndTime()) || course.getAvailableStartTime().equals(course.getAvailableEndTime()))) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Available end time must be after start time.");
        }

        Course updatedCourse = courseRepository.save(course);
        log.info("Course updated: {}", updatedCourse.getId());
        return mapToDto(updatedCourse);
    }

    @Transactional
    public void deleteCourse(String adminEmail, UUID id) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(course.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to delete this course.");
        }

        // TODO: Add logic to prevent deletion if bookings are linked to this course
        courseRepository.delete(course);
        log.info("Course deleted: {}", id);
    }

    private CourseDto mapToDto(Course course) {
        CourseDto dto = modelMapper.map(course, CourseDto.class);
        dto.setParlourId(course.getParlour().getId());
        dto.setCategoryName(course.getCategory().getName());
        return dto;
    }
}
