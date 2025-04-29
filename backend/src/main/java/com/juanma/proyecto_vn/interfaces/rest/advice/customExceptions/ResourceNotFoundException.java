package com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}