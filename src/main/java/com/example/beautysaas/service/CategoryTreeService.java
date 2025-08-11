package com.example.beautysaas.service;

import com.example.beautysaas.dto.category.CategoryTreeDTO;
import com.example.beautysaas.entity.Category;
import com.example.beautysaas.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryTreeService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<CategoryTreeDTO> getCategoryTree(UUID parlourId) {
        List<Category> rootCategories = categoryRepository.findRootCategories(parlourId);
        return buildCategoryTree(rootCategories);
    }

    @Transactional(readOnly = true)
    public CategoryTreeDTO getCategorySubtree(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        return buildCategoryTreeDTO(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryTreeDTO> getCategoriesByLevel(UUID parlourId, Integer level) {
        return categoryRepository.findByLevel(parlourId, level)
                .stream()
                .map(category -> modelMapper.map(category, CategoryTreeDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Integer> getMaxLevel(UUID parlourId) {
        return categoryRepository.findMaxLevel(parlourId);
    }

    private List<CategoryTreeDTO> buildCategoryTree(List<Category> categories) {
        return categories.stream()
                .map(this::buildCategoryTreeDTO)
                .collect(Collectors.toList());
    }

    private CategoryTreeDTO buildCategoryTreeDTO(Category category) {
        CategoryTreeDTO dto = modelMapper.map(category, CategoryTreeDTO.class);
        if (!category.getChildren().isEmpty()) {
            dto.setChildren(buildCategoryTree(category.getChildren()));
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public List<CategoryTreeDTO> searchCategoryTree(UUID parlourId, String searchTerm) {
        List<Category> allCategories = categoryRepository.findByParlourId(parlourId, null).getContent();
        
        // Filter categories that match the search term
        List<Category> matchingCategories = allCategories.stream()
                .filter(category -> categoryMatchesSearch(category, searchTerm))
                .collect(Collectors.toList());

        // Find all parent categories of matching categories
        Set<Category> treeCategories = new HashSet<>(matchingCategories);
        matchingCategories.forEach(category -> addParentsToSet(category, treeCategories));

        // Build trees starting from root categories
        return treeCategories.stream()
                .filter(category -> category.getParent() == null)
                .map(this::buildFilteredCategoryTreeDTO)
                .collect(Collectors.toList());
    }

    private boolean categoryMatchesSearch(Category category, String searchTerm) {
        String search = searchTerm.toLowerCase();
        return category.getName().toLowerCase().contains(search) ||
               (category.getDescription() != null && 
                category.getDescription().toLowerCase().contains(search));
    }

    private void addParentsToSet(Category category, Set<Category> categories) {
        Category parent = category.getParent();
        while (parent != null) {
            categories.add(parent);
            parent = parent.getParent();
        }
    }

    private CategoryTreeDTO buildFilteredCategoryTreeDTO(Category category) {
        CategoryTreeDTO dto = modelMapper.map(category, CategoryTreeDTO.class);
        if (!category.getChildren().isEmpty()) {
            List<CategoryTreeDTO> childrenDTOs = category.getChildren().stream()
                    .map(this::buildFilteredCategoryTreeDTO)
                    .filter(childDto -> childDto.getChildren() != null && 
                                      !childDto.getChildren().isEmpty())
                    .collect(Collectors.toList());
            if (!childrenDTOs.isEmpty()) {
                dto.setChildren(childrenDTOs);
            }
        }
        return dto;
    }
}
