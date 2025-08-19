package com.beautyparlour.service;

import com.beautyparlour.dto.request.CreateCourseRequest;
import com.beautyparlour.entity.Course;
import com.beautyparlour.exception.ResourceNotFoundException;
import com.beautyparlour.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public Course createCourse(CreateCourseRequest request, UUID parlourId) {
        Course course = new Course(
                parlourId,
                request.getName(),
                request.getImageUrl(),
                request.getDescription(),
                request.getPrice()
        );
        return courseRepository.save(course);
    }

    public List<Course> getCoursesByParlour(UUID parlourId) {
        return courseRepository.findByParlourId(parlourId);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public void deleteCourse(UUID courseId, UUID parlourId) {
        Course course = courseRepository.findByIdAndParlourId(courseId, parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        courseRepository.delete(course);
    }
}
