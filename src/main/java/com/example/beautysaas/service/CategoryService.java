package com.example.beautysaas.service;

import com.example.beautysaas.dto.category.CategoryCreateRequest;
import com.example.beautysaas.dto.category.CategoryDto;
import com.example.beautysaas.dto.category.CategoryReorderRequest;
import com.example.beautysaas.dto.category.CategoryStatsDto;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public CategoryStatsDto getCategoryStatistics(String adminEmail, UUID parlourId) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Long totalCategories = categoryRepository.countTotalCategories(parlourId);
        Long activeCategories = categoryRepository.countActiveCategories(parlourId);
        Long deletedCategories = categoryRepository.countDeletedCategories(parlourId);
        Integer maxLevel = categoryRepository.getMaxLevelForParlour(parlourId).orElse(0);

        // Build level counts map
        Map<Integer, Long> levelCounts = new HashMap<>();
        List<Object[]> levelCountsRaw = categoryRepository.countByLevelRaw(parlourId);
        for (Object[] row : levelCountsRaw) {
            Integer level = (Integer) row[0];
            Long count = (Long) row[1];
            levelCounts.put(level, count);
        }

        CategoryStatsDto stats = CategoryStatsDto.builder()
                .totalCategories(totalCategories != null ? totalCategories : 0L)
                .activeCategories(activeCategories != null ? activeCategories : 0L)
                .deletedCategories(deletedCategories != null ? deletedCategories : 0L)
                .maxLevel(maxLevel)
                .categoriesPerLevel(levelCounts)
                .lastUpdated(LocalDateTime.now())
                .build();

        log.info("Retrieved category statistics for parlour {}: {} total, {} active, {} deleted", 
                parlourId, stats.getTotalCategories(), stats.getActiveCategories(), stats.getDeletedCategories());

        return stats;
    }

    @Transactional
    public void bulkUpdateStatus(String adminEmail, UUID parlourId, List<UUID> categoryIds, boolean active) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        int updatedCount = categoryRepository.bulkUpdateStatus(
            categoryIds,
            parlourId,
            active,
            LocalDateTime.now(),
            adminEmail
        );

        log.info("Bulk updated {} categories status to active={} by admin {}", updatedCount, active, adminEmail);
    }

    @Transactional(readOnly = true)
    public Page<CategoryDto> searchByMetadata(String adminEmail, UUID parlourId, String metaKeywords, String colorCode, Pageable pageable) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        Page<Category> categories = categoryRepository.findByMetadata(parlourId, metaKeywords, colorCode, pageable);
        return categories.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoriesByDepth(String adminEmail, UUID parlourId, int depth) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        if (depth < 0) {
            throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Depth must be non-negative.");
        }

        List<Category> categories = categoryRepository.findByPathDepth(parlourId, depth);
        return categories.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void reorderCategories(String adminEmail, UUID parlourId, UUID parentId, List<CategoryReorderRequest> reorderRequests) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", adminEmail));

        if (!admin.getRole().getName().equals("ADMIN") || !admin.getParlour().getId().equals(parlourId)) {
            throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "User is not an Admin or not authorized for this parlour.");
        }

        // Validate all categories belong to the parlour and have the correct parent
        for (CategoryReorderRequest request : reorderRequests) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

            if (!category.getParlour().getId().equals(parlourId)) {
                throw new BeautySaasApiException(HttpStatus.FORBIDDEN, "Category does not belong to the specified parlour.");
            }

            UUID categoryParentId = category.getParent() != null ? category.getParent().getId() : null;
            if (!Objects.equals(categoryParentId, parentId)) {
                throw new BeautySaasApiException(HttpStatus.BAD_REQUEST, "Category parent mismatch.");
            }
        }

        // Update display orders
        LocalDateTime now = LocalDateTime.now();
        for (CategoryReorderRequest request : reorderRequests) {
            Category category = categoryRepository.findById(request.getCategoryId()).get();
            category.setDisplayOrder(request.getDisplayOrder());
            category.setUpdatedAt(now);
            category.setUpdatedBy(adminEmail);
            categoryRepository.save(category);
        }

        log.info("Reordered {} categories under parent {} for parlour {} by admin {}", 
                reorderRequests.size(), parentId, parlourId, adminEmail);
    }
}
