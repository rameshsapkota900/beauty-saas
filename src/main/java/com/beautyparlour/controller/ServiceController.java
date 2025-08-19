package com.beautyparlour.controller;

import com.beautyparlour.dto.request.CreateServiceRequest;
import com.beautyparlour.dto.response.ApiResponse;
import com.beautyparlour.dto.response.ServiceDTO;
import com.beautyparlour.entity.Service;
import com.beautyparlour.security.UserPrincipal;
import com.beautyparlour.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/services")
@Tag(name = "Services", description = "Service management APIs")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @PostMapping
    @Operation(summary = "Create new service")
    public ResponseEntity<ApiResponse<ServiceDTO>> createService(
            @Valid @RequestBody CreateServiceRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Service service = serviceService.createService(request, currentUser.getParlourId());
        ServiceDTO serviceDTO = new ServiceDTO(service);
        return ResponseEntity.ok(ApiResponse.success("Service created successfully", serviceDTO));
    }

    @GetMapping
    @Operation(summary = "Get services")
    public ResponseEntity<ApiResponse<List<ServiceDTO>>> getServices(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<Service> services;
        if (currentUser != null && currentUser.getParlourId() != null) {
            services = serviceService.getServicesByParlour(currentUser.getParlourId());
        } else {
            services = serviceService.getAllServices();
        }
        List<ServiceDTO> serviceDTOs = services.stream()
                .map(ServiceDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Services retrieved successfully", serviceDTOs));
    }

    @DeleteMapping("/{serviceId}")
    @Operation(summary = "Delete service")
    public ResponseEntity<ApiResponse<Void>> deleteService(
            @PathVariable UUID serviceId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        serviceService.deleteService(serviceId, currentUser.getParlourId());
        return ResponseEntity.ok(ApiResponse.success("Service deleted successfully"));
    }
}
