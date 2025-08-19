package com.beautyparlour.service;

import com.beautyparlour.dto.request.CreateCategoryRequest;
import com.beautyparlour.entity.Category;
import com.beautyparlour.exception.ResourceNotFoundException;
import com.beautyparlour.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory(CreateCategoryRequest request, UUID parlourId) {
        Category category = new Category(parlourId, request.getName());
        return categoryRepository.save(category);
    }

    public List<Category> getCategoriesByParlour(UUID parlourId) {
        return categoryRepository.findByParlourId(parlourId);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public void deleteCategory(UUID categoryId, UUID parlourId) {
        Category category = categoryRepository.findByIdAndParlourId(categoryId, parlourId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        categoryRepository.delete(category);
    }
}
