package com.juanma.proyecto_vn.Application.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.juanma.proyecto_vn.infrastructure.persistence.repository.JpaUserRepository;
import com.juanma.proyecto_vn.interfaces.rest.validation.UniqueEmail;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private JpaUserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        // Si el email es nulo, no validamos (otras validaciones se encargarán)
        if (email == null) {
            return true;
        }

        // Verificar si ya existe un usuario con ese email
        return userRepository.findByEmail(email).isEmpty();
    }
}