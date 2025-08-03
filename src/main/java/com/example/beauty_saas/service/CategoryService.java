package com.example.beauty_saas.service;

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
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Parlour parlour = parlourRepository.findById(parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Parlour", "id", parlourId));

        if (categoryRepository.existsByParlourIdAndNameIgnoreCase(parlourId, createRequest.getName())) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Category with name '" + createRequest.getName() + "' already exists in this parlour.");
        }

        Category category = modelMapper.map(createRequest, Category.class);
        category.setParlour(parlour);

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created: {}", savedCategory.getId());
        return mapToDto(savedCategory);
    }

    public Page<CategoryDto> listCategories(UUID parlourId, Pageable pageable) {
        // Publicly accessible, so no admin check needed
        Page<Category> categories = categoryRepository.findByParlourId(parlourId, pageable);
        return categories.map(this::mapToDto);
    }

    public CategoryDto getCategoryById(String userEmail, UUID parlourId, UUID categoryId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        if (!category.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Category does not belong to the specified parlour.");
        }

        // Ensure user is authorized to view this parlour's data
        if (user.getRole().getName().equals("ADMIN") && (user.getParlour() == null || !user.getParlour().getId().equals(parlourId))) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "Admin is not authorized for this parlour.");
        }
        // Customers can view any parlour's categories for now, but this can be restricted if needed.

        log.debug("Fetching category: {}", categoryId);
        return mapToDto(category);
    }

    public Page<CategoryDto> getAllCategories(String userEmail, UUID parlourId, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        // Ensure user is authorized to view this parlour's data
        if (user.getRole().getName().equals("ADMIN") && (user.getParlour() == null || !user.getParlour().getId().equals(parlourId))) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "Admin is not authorized for this parlour.");
        }
        // Customers can view any parlour's categories for now, but this can be restricted if needed.

        log.debug("Fetching all categories for parlour: {}", parlourId);
        Page<Category> categories = categoryRepository.findByParlourId(parlourId, pageable);
        return categories.map(this::mapToDto);
    }

    @Transactional
    public CategoryDto updateCategory(String adminEmail, UUID parlourId, UUID categoryId, CategoryUpdateRequest updateRequest) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        if (!category.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Category does not belong to the specified parlour.");
        }

        if (updateRequest.getName() != null && !updateRequest.getName().equalsIgnoreCase(category.getName())) {
            if (categoryRepository.existsByParlourIdAndNameIgnoreCase(parlourId, updateRequest.getName())) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Category with name '" + updateRequest.getName() + "' already exists in this parlour.");
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
    public void deleteCategory(String adminEmail, UUID parlourId, UUID categoryId) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));
        if (!admin.getRole().getName().equals("ADMIN") || admin.getParlour() == null || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        if (!category.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Category does not belong to the specified parlour.");
        }

        // TODO: Add logic to prevent deletion if services/products are still linked to this category
        // For now, it will likely cause a foreign key constraint violation if not handled by DB cascade rules.
        // A better approach would be to check dependencies and throw an error or reassign.

        categoryRepository.delete(category);
        log.info("Category deleted: {}", categoryId);
    }

    private CategoryDto mapToDto(Category category) {
        CategoryDto dto = modelMapper.map(category, CategoryDto.class);
        dto.setParlourId(category.getParlour().getId());
        return dto;
    }
}
