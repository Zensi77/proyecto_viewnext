package com.juanma.proyecto_vn.Application.validator;

import com.juanma.proyecto_vn.domain.repository.CategoryRepository;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Validador para categorías
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    /**
     * Valida que no exista otra categoría con el mismo nombre
     * 
     * @param name Nombre de la categoría a validar
     */
    public void validateUniqueName(String name) {
        log.debug("Validando que no exista otra categoría con el nombre: {}", name);
        if (categoryRepository.findByName(name) != null) {
            log.warn("Ya existe una categoría con el nombre: {}", name);
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + name);
        }
    }

    /**
     * Valida que exista una categoría con el ID proporcionado
     * 
     * @param id ID de la categoría a validar
     */
    public void validateCategoryExists(UUID id) {
        log.debug("Validando que exista la categoría con ID: {}", id);
        if (categoryRepository.findById(id) == null) {
            log.warn("No se encontró la categoría con ID: {}", id);
            throw new ResourceNotFoundException("No se encontró la categoría con ID: " + id);
        }
    }
}