package com.juanma.proyecto_vn.interfaces.rest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import com.juanma.proyecto_vn.Application.validator.UniqueEmailValidator;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
@Documented
public @interface UniqueEmail {
    String message() default "El email ya está en uso";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}