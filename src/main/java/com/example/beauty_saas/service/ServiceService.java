package com.example.beauty_saas.service;

import com.example.beautysaas.dto.service.ServiceCreateRequest;
import com.example.beautysaas.dto.service.ServiceDto;
import com.example.beautysaas.dto.service.ServiceUpdateRequest;
import com.example.beautysaas.entity.Category;
import com.example.beautysaas.entity.Parlour;
import com.example.beautysaas.entity.Service;
import com.example.beautysaas.entity.User;
import com.example.beautysaas.exception.BeautySaasApiException;
import com.example.beautysaas.exception.ResourceNotFoundException;
import com.example.beautysaas.repository.CategoryRepository;
import com.example.beautysaas.repository.ParlourRepository;
import com.example.beautysaas.repository.ServiceRepository;
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
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ParlourRepository parlourRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ServiceService(ServiceRepository serviceRepository, ParlourRepository parlourRepository, CategoryRepository categoryRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.serviceRepository = serviceRepository;
        this.parlourRepository = parlourRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ServiceDto createService(String adminEmail, UUID parlourId, ServiceCreateRequest createRequest) {
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

        if (serviceRepository.existsByParlourIdAndNameIgnoreCase(parlourId, createRequest.getName())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Service with name '" + createRequest.getName() + "' already exists in this parlour.");
        }

        Service service = modelMapper.map(createRequest, Service.class);
        service.setParlour(parlour);
        service.setCategory(category);

        Service savedService = serviceRepository.save(service);
        log.info("Service created: {}", savedService.getId());
        return mapToDto(savedService);
    }

    public ServiceDto getServiceById(String userEmail, UUID parlourId, UUID serviceId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", serviceId));

        if (!service.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Service does not belong to the specified parlour.");
        }

        // Ensure user is authorized to view this parlour's data
        if (user.getRole().getName().equals("ADMIN") && (user.getParlour() == null || !user.getParlour().getId().equals(parlourId))) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "Admin is not authorized for this parlour.");
        }
        // Customers can view any parlour's services for now, but this can be restricted if needed.

        log.debug("Fetching service: {}", serviceId);
        return mapToDto(service);
    }

    public Page<ServiceDto> getAllServices(String userEmail, UUID parlourId, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        // Ensure user is authorized to view this parlour's data
        if (user.getRole().getName().equals("ADMIN") && (user.getParlour() == null || !user.getParlour().getId().equals(parlourId))) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "Admin is not authorized for this parlour.");
        }
        // Customers can view any parlour's services for now, but this can be restricted if needed.

        log.debug("Fetching all services for parlour: {}", parlourId);
        Page<Service> services = serviceRepository.findByParlourId(parlourId, pageable);
        return services.map(this::mapToDto);
    }

    @Transactional
    public ServiceDto updateService(String adminEmail, UUID parlourId, UUID serviceId, ServiceUpdateRequest updateRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", serviceId));

        if (!service.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Service does not belong to the specified parlour.");
        }

        if (updateRequest.getName() != null && !updateRequest.getName().equalsIgnoreCase(service.getName())) {
            if (serviceRepository.existsByParlourIdAndNameIgnoreCase(parlourId, updateRequest.getName())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Service with name '" + updateRequest.getName() + "' already exists in this parlour.");
            }
            service.setName(updateRequest.getName());
        }
        if (updateRequest.getDescription() != null) {
            service.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getPrice() != null) {
            service.setPrice(updateRequest.getPrice());
        }
        if (updateRequest.getDurationMinutes() != null) {
            service.setDurationMinutes(updateRequest.getDurationMinutes());
        }
        if (updateRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", updateRequest.getCategoryId()));
            if (!category.getParlour().getId().equals(parlourId)) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Category does not belong to the specified parlour.");
            }
            service.setCategory(category);
        }

        Service updatedService = serviceRepository.save(service);
        log.info("Service updated: {}", updatedService.getId());
        return mapToDto(updatedService);
    }

    @Transactional
    public void deleteService(String adminEmail, UUID parlourId, UUID serviceId) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", serviceId));

        if (!service.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Service does not belong to the specified parlour.");
        }

        // TODO: Add logic to prevent deletion if bookings are still linked to this service
        // For now, it will likely cause a foreign key constraint violation if not handled by DB cascade rules.

        serviceRepository.delete(service);
        log.info("Service deleted: {}", serviceId);
    }

    private ServiceDto mapToDto(Service service) {
        ServiceDto dto = modelMapper.map(service, ServiceDto.class);
        dto.setParlourId(service.getParlour().getId());
        dto.setParlourName(service.getParlour().getName());
        dto.setCategoryName(service.getCategory().getName());
        return dto;
    }
}
