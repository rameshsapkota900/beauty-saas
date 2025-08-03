package com.example.beauty_saas.service;

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
    public CourseDto createCourse(String adminEmail, UUID parlourId, CourseCreateRequest createRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));
        Category category = categoryRepository.findById(createRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", createRequest.getCategoryId()));

        if (!category.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Category does not belong to the specified parlour.");
        }

        if (courseRepository.existsByParlourIdAndNameIgnoreCase(parlourId, createRequest.getName())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Course with name '" + createRequest.getName() + "' already exists in this parlour.");
        }

        Course course = modelMapper.map(createRequest, Course.class);
        course.setParlour(parlour);
        course.setCategory(category);

        Course savedCourse = courseRepository.save(course);
        log.info("Course created: {}", savedCourse.getId());
        return mapToDto(savedCourse);
    }

    public CourseDto getCourseById(String userEmail, UUID parlourId, UUID courseId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (!course.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Course does not belong to the specified parlour.");
        }

        // Ensure user is authorized to view this parlour's data
        if (user.getRole().getName().equals("ADMIN") && (user.getParlour() == null || !user.getParlour().getId().equals(parlourId))) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "Admin is not authorized for this parlour.");
        }
        // Customers can view any parlour's courses for now, but this can be restricted if needed.

        log.debug("Fetching course: {}", courseId);
        return mapToDto(course);
    }

    public Page<CourseDto> getAllCourses(String userEmail, UUID parlourId, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        // Ensure user is authorized to view this parlour's data
        if (user.getRole().getName().equals("ADMIN") && (user.getParlour() == null || !user.getParlour().getId().equals(parlourId))) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "Admin is not authorized for this parlour.");
        }
        // Customers can view any parlour's courses for now, but this can be restricted if needed.

        log.debug("Fetching all courses for parlour: {}", parlourId);
        Page<Course> courses = courseRepository.findByParlourId(parlourId, pageable);
        return courses.map(this::mapToDto);
    }

    @Transactional
    public CourseDto updateCourse(String adminEmail, UUID parlourId, UUID courseId, CourseUpdateRequest updateRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (!course.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Course does not belong to the specified parlour.");
        }

        if (updateRequest.getName() != null && !updateRequest.getName().equalsIgnoreCase(course.getName())) {
            if (courseRepository.existsByParlourIdAndNameIgnoreCase(parlourId, updateRequest.getName())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Course with name '" + updateRequest.getName() + "' already exists in this parlour.");
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
            if (!category.getParlour().getId().equals(parlourId)) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Category does not belong to the specified parlour.");
            }
            course.setCategory(category);
        }

        Course updatedCourse = courseRepository.save(course);
        log.info("Course updated: {}", updatedCourse.getId());
        return mapToDto(updatedCourse);
    }

    @Transactional
    public void deleteCourse(String adminEmail, UUID parlourId, UUID courseId) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (!course.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Course does not belong to the specified parlour.");
        }

        // TODO: Add logic to prevent deletion if bookings or successful students are still linked to this course
        // For now, it will likely cause a foreign key constraint violation if not handled by DB cascade rules.

        courseRepository.delete(course);
        log.info("Course deleted: {}", courseId);
    }

    public long countCoursesByParlourId(UUID parlourId) {
        return courseRepository.countByParlourId(parlourId);
    }

    private CourseDto mapToDto(Course course) {
        CourseDto dto = modelMapper.map(course, CourseDto.class);
        dto.setParlourId(course.getParlour().getId());
        dto.setParlourName(course.getParlour().getName());
        dto.setCategoryName(course.getCategory().getName());
        return dto;
    }
}
