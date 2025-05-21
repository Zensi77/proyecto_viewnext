package com.juanma.proyecto_vn.Application.validator;

import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.repository.ProviderRepository;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Validador para proveedores
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProviderValidator {
    private final ProviderRepository providerRepository;

    /**
     * Valida que exista un proveedor con el nombre proporcionado
     * 
     * @param name Nombre del proveedor a validar
     * @throws ResourceNotFoundException si no existe el proveedor
     */
    public void validateProviderExists(String name) {
        if (!providerRepository.findByNameContaining(name).isEmpty()) {
            log.warn("No se encontró ningún proveedor con el nombre: {}", name);
            throw new ResourceNotFoundException("Ya existe algun proveedor con el nombre: " + name);
        }
    }

    /**
     * Valida que un nombre de proveedor no esté en uso
     * 
     * @param name Nombre del proveedor a validar
     * @throws IllegalArgumentException si ya existe un proveedor con ese nombre
     */
    public void validateProviderNameNotInUse(String name) {
        if (!providerRepository.findByNameContaining(name).isEmpty()) {
            log.warn("Ya existe un proveedor con el nombre: {}", name);
            throw new IllegalArgumentException("Ya existe un proveedor con el nombre: " + name);
        }
    }
}
