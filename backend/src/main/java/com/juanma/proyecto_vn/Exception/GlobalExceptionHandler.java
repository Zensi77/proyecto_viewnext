package com.juanma.proyecto_vn.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

        // Para errores de validación de datos
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Object> handleValidationExceptions(
                        MethodArgumentNotValidException ex, WebRequest request) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .message("Error de validación")
                                .errors(errors)
                                .path(request.getDescription(false))
                                .build();

                return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }

        // Para errores de UUID o formato incorrecto
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Object> handleIllegalArgumentException(
                        IllegalArgumentException ex, WebRequest request) {

                Map<String, String> errors = new HashMap<>();
                errors.put("error", ex.getMessage());

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .message("Error de formato")
                                .errors(errors)
                                .path(request.getDescription(false))
                                .build();

                return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }

        // Excepción personalizada para "método o URL no encontrado"
        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<Object> handleNoHandlerFoundException(
                        NoHandlerFoundException ex, WebRequest request) {

                Map<String, String> errors = Map.of("error", "El recurso solicitado no existe: " + ex.getRequestURL());

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .message("Método o URL no encontrado")
                                .errors(errors)
                                .path(request.getDescription(false))
                                .build();

                return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<Object> handleResourceNotFoundException(
                        ResourceNotFoundException ex, WebRequest request) {

                Map<String, String> errors = new HashMap<>();
                errors.put("error", ex.getMessage());

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .message("Recurso no encontrado")
                                .errors(errors)
                                .path(request.getDescription(false))
                                .build();

                return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(
                        MethodArgumentTypeMismatchException ex, WebRequest request) {

                Map<String, String> errors = new HashMap<>();
                errors.put("error", "Tipo de argumento no válido: " + ex.getMessage());

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .message("Tipo de argumento no válido")
                                .errors(errors)
                                .path(request.getDescription(false))
                                .build();

                return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(NoStockException.class)
        public ResponseEntity<Object> handleNoStockException(
                        NoStockException ex, WebRequest request) {

                Map<String, String> errors = new HashMap<>();
                errors.put("error", ex.getMessage());

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .message("No hay stock disponible")
                                .errors(errors)
                                .path(request.getDescription(false))
                                .build();

                return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }


        // Excepciones genéricas
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Object> handleAllExceptions(
                        Exception ex, WebRequest request) {

                Map<String, String> errors = new HashMap<>();
                errors.put("error", ex.getMessage());
                errors.put("Error Class", ex.getClass().getName());

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .message("Error interno del servidor")
                                .errors(errors)
                                .path(request.getDescription(false))
                                .build();

                return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);

        }

}