package com.juanma.proyecto_vn.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
@Documented
public @interface UniqueEmail {
    String message() default "El email ya está en uso";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}