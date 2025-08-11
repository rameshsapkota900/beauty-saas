package com.example.beautysaas.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueSlugValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueSlug {
    String message() default "Slug must be unique within the parlour";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
