package com.example.beautysaas.validation;

import com.example.beautysaas.repository.CategoryRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueSlugValidator implements ConstraintValidator<UniqueSlug, String> {

    private final CategoryRepository categoryRepository;

    @Override
    public void initialize(UniqueSlug constraintAnnotation) {
    }

    @Override
    public boolean isValid(String slug, ConstraintValidatorContext context) {
        if (slug == null) {
            return true;
        }
        return !categoryRepository.existsBySlug(slug);
    }
}
