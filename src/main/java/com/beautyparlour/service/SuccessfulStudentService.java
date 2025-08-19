package com.beautyparlour.service;

import com.beautyparlour.dto.request.CreateSuccessfulStudentRequest;
import com.beautyparlour.entity.SuccessfulStudent;
import com.beautyparlour.exception.ResourceNotFoundException;
import com.beautyparlour.repository.SuccessfulStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SuccessfulStudentService {

    @Autowired
    private SuccessfulStudentRepository successfulStudentRepository;

    public SuccessfulStudent createSuccessfulStudent(CreateSuccessfulStudentRequest request, UUID parlourId) {
        SuccessfulStudent student = new SuccessfulStudent(
                parlourId,
                request.getName(),
                request.getImageUrl()
        );
        return successfulStudentRepository.save(student);
    }

    public List<SuccessfulStudent> getSuccessfulStudentsByParlour(UUID parlourId) {
        return successfulStudentRepository.findByParlourId(parlourId);
    }

    public List<SuccessfulStudent> getAllSuccessfulStudents() {
        return successfulStudentRepository.findAll();
    }

    public void deleteSuccessfulStudent(UUID studentId, UUID parlourId) {
        SuccessfulStudent student = successfulStudentRepository.findByIdAndParlourId(studentId, parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Successful student not found"));
        successfulStudentRepository.delete(student);
    }
}
