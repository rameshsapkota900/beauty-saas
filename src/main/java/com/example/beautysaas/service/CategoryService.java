package com.example.beautysaas.service;

import com.example.beautysaas.dto.category.CategoryCreateRequest;
import com.example.beautysaas.dto.category.CategoryDto;
import com.example.beautysaas.dto.category.CategoryUpdateRequest;
import com.example.beautysaas.entity.Category;
import com.example.beautysaas.entity.Parlour;
import com.example.beautysaas.entity.User;
import com.example.beautysaas.exception.BeautySaasApiException;
import com.example.beautysaas.exception.ResourceNotFoundException;
import com.example.beautysaas.repository.CategoryRepository;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ParlourRepository parlourRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CategoryService(CategoryRepository categoryRepository, ParlourRepository parlourRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.parlourRepository = parlourRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public CategoryDto createCategory(String adminEmail, UUID parlourId, CategoryCreateRequest createRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not associated with this parlour.");
        }

        if (categoryRepository.existsByParlourIdAndNameIgnoreCase(parlourId, createRequest.getName())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Category with this name already exists for this parlour.");
        }

        Category category = Category.builder()
                .parlour(parlour)
                .name(createRequest.getName())
                .description(createRequest.getDescription())
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created: {}", savedCategory.getId());
        return mapToDto(savedCategory);
    }

    public Page<CategoryDto> listCategories(UUID parlourId, Pageable pageable) {
        // Publicly accessible, so no admin check needed
        Page<Category> categories = categoryRepository.findByParlourId(parlourId, pageable);
        return categories.map(this::mapToDto);
    }

    public CategoryDto getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return mapToDto(category);
    }

    @Transactional
    public CategoryDto updateCategory(String adminEmail, UUID id, CategoryUpdateRequest updateRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(category.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to update this category.");
        }

        if (updateRequest.getName() != null && !updateRequest.getName().equalsIgnoreCase(category.getName())) {
            if (categoryRepository.existsByParlourIdAndNameIgnoreCase(category.getParlour().getId(), updateRequest.getName())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Category with this name already exists for this parlour.");
            }
            category.setName(updateRequest.getName());
        }
        if (updateRequest.getDescription() != null) {
            category.setDescription(updateRequest.getDescription());
        }

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated: {}", updatedCategory.getId());
        return mapToDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(String adminEmail, UUID id) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(category.getParlour().getId())) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized to delete this category.");
        }

        // TODO: Add logic to prevent deletion if services/courses/products are linked to this category
        // For production, you might want to disallow deletion or reassign items to a default category.
        // For now, it will cascade delete if configured in JPA or throw an error if not.
        categoryRepository.delete(category);
        log.info("Category deleted: {}", id);
    }

    private CategoryDto mapToDto(Category category) {
        CategoryDto dto = modelMapper.map(category, CategoryDto.class);
        dto.setParlourId(category.getParlour().getId());
        return dto;
    }
}
