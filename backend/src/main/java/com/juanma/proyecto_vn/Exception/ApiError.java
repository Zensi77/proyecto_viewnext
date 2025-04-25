package com.juanma.proyecto_vn.Exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * Clase que representa la estructura estándar de los errores en la API.
 */
@Data
@Builder
public class ApiError {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<String> validationErrors;
    private Map<String, Object> errors;
    private Throwable cause; // Nuevo campo
}