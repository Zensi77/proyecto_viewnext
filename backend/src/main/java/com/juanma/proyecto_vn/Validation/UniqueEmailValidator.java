package com.juanma.proyecto_vn.Validation;

import com.juanma.proyecto_vn.Repositorys.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        // Si el email es nulo, no validamos (otras validaciones se encargarán)
        if (email == null) {
            return true;
        }

        // Verificar si ya existe un usuario con ese email
        return userRepository.findByEmail(email) == null;
    }
}