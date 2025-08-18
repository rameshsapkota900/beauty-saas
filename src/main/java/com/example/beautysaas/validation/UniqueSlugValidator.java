package com.example.beautysaas.validation;

import com.example.beautysaas.repository.CategoryRepository;
import com.example.beautysaas.service.CurrentParlourResolver;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UniqueSlugValidator implements ConstraintValidator<UniqueSlug, String> {

    private final CategoryRepository categoryRepository;
    private final CurrentParlourResolver currentParlourResolver;

    @Override
    public void initialize(UniqueSlug constraintAnnotation) {
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValid(String slug, ConstraintValidatorContext context) {
        if (slug == null || slug.trim().isEmpty()) {
            return true; // Let @NotBlank handle empty values
        }

        try {
            UUID parlourId = currentParlourResolver.getCurrentParlourId();
            if (parlourId == null) {
                log.warn("No parlour context found while validating slug: {}", slug);
                return false;
            }

            return !categoryRepository.existsBySlugAndParlourId(slug, parlourId, null);
        } catch (Exception e) {
            log.error("Error while validating slug uniqueness: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to check slug uniqueness when updating an existing category
     * @param slug The slug to check
     * @param parlourId The parlour ID
     * @param excludeCategoryId The category ID to exclude from the check (for updates)
     * @return true if the slug is unique within the parlour context
     */
    public boolean isSlugUniqueForUpdate(String slug, UUID parlourId, UUID excludeCategoryId) {
        try {
            return !categoryRepository.existsBySlugAndParlourId(slug, parlourId, excludeCategoryId);
        } catch (Exception e) {
            log.error("Error while validating slug uniqueness for update: {}", e.getMessage());
            return false;
        }
    }
}
