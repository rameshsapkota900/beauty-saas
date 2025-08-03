package com.example.beautysaas.service;

import com.example.beautysaas.dto.service.ServiceCreateRequest;
import com.example.beautysaas.dto.service.ServiceDto;
import com.example.beautysaas.dto.service.ServiceUpdateRequest;
import com.example.beautysaas.entity.Category;
import com.example.beautysaas.entity.Parlour;
import com.example.beautysaas.entity.Role;
import com.example.beautysaas.entity.Service;
import com.example.beautysaas.entity.User;
import com.example.beautysaas.exception.BeautySaasApiException;
import com.example.beautysaas.exception.ResourceNotFoundException;
import com.example.beautysaas.repository.CategoryRepository;
import com.example.beautysaas.repository.ParlourRepository;
import com.example.beautysaas.repository.ServiceRepository;
import com.example.beautysaas.repository.UserRepository;
import com.example.beautysaas.security.UserPrincipal;
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
    public ServiceDto addService(String adminEmail, UUID parlourId, ServiceCreateRequest createRequest) {
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
        if (serviceRepository.existsByParlourIdAndNameIgnoreCase(parlourId, createRequest.getName())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Service with this name already exists for this parlour.");
        }
        if (createRequest.getAvailableStartTime().isAfter(createRequest.getAvailableEndTime()) || createRequest.getAvailableStartTime().equals(createRequest.getAvailableEndTime())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Available end time must be after start time.");
        }

        Service service = Service.builder()
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

        Service savedService = serviceRepository.save(service);
        log.info("Service added: {}", savedService.getId());
        return mapToDto(savedService);
    }

    public Page<ServiceDto> listServices(UUID parlourId, Pageable pageable) {
        Page<Service> services = serviceRepository.findByParlourId(parlourId, pageable);
        return services.map(this::mapToDto);
    }

    public ServiceDto getServiceDetail(UUID id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));
        return mapToDto(service);
    }

    @Transactional
    public ServiceDto updateService(String adminEmail, UUID id, ServiceUpdateRequest updateRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(service.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to update this service.");
        }

        if (updateRequest.getName() != null && !updateRequest.getName().equalsIgnoreCase(service.getName())) {
            if (serviceRepository.existsByParlourIdAndNameIgnoreCase(service.getParlour().getId(), updateRequest.getName())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Service with this name already exists for this parlour.");
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
            if (!category.getParlour().getId().equals(service.getParlour().getId())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "New category does not belong to the same parlour.");
            }
            service.setCategory(category);
        }
        if (updateRequest.getIsActive() != null) {
            service.setIsActive(updateRequest.getIsActive());
        }
        if (updateRequest.getAvailableStartTime() != null) {
            service.setAvailableStartTime(updateRequest.getAvailableStartTime());
        }
        if (updateRequest.getAvailableEndTime() != null) {
            service.setAvailableEndTime(updateRequest.getAvailableEndTime());
        }
        if (service.getAvailableStartTime() != null && service.getAvailableEndTime() != null &&
                (service.getAvailableStartTime().isAfter(service.getAvailableEndTime()) || service.getAvailableStartTime().equals(service.getAvailableEndTime()))) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Available end time must be after start time.");
        }

        Service updatedService = serviceRepository.save(service);
        log.info("Service updated: {}", updatedService.getId());
        return mapToDto(updatedService);
    }

    @Transactional
    public void deleteService(String adminEmail, UUID id) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", "id", id));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(service.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to delete this service.");
        }

        // TODO: Add logic to prevent deletion if bookings are linked to this service
        serviceRepository.delete(service);
        log.info("Service deleted: {}", id);
    }

    private ServiceDto mapToDto(Service service) {
        ServiceDto dto = modelMapper.map(service, ServiceDto.class);
        dto.setParlourId(service.getParlour().getId());
        dto.setCategoryName(service.getCategory().getName());
        return dto;
    }
}
