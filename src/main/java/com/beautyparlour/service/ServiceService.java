package com.beautyparlour.service;

import com.beautyparlour.dto.request.CreateServiceRequest;
import com.beautyparlour.entity.Service;
import com.beautyparlour.exception.ResourceNotFoundException;
import com.beautyparlour.repository.CategoryRepository;
import com.beautyparlour.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Service createService(CreateServiceRequest request, UUID parlourId) {
        // Verify category belongs to the same parlour
        categoryRepository.findByIdAndParlourId(request.getCategoryId(), parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Service service = new Service(
                parlourId,
                request.getCategoryId(),
                request.getName(),
                request.getImageUrl(),
                request.getPrice()
        );
        return serviceRepository.save(service);
    }

    public List<Service> getServicesByParlour(UUID parlourId) {
        return serviceRepository.findByParlourId(parlourId);
    }

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public void deleteService(UUID serviceId, UUID parlourId) {
        Service service = serviceRepository.findByIdAndParlourId(serviceId, parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        serviceRepository.delete(service);
    }
}
