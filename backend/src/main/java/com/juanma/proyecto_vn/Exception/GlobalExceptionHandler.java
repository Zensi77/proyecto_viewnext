package com.juanma.proyecto_vn.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .message("Error de validación")
                                .build();

                return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<Object> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException ex, WebRequest request) {

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .message("Error de formato en el cuerpo de la petición")
                                .build();

                return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }

        // Para errores de UUID o formato incorrecto
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Object> handleIllegalArgumentException(
                        IllegalArgumentException ex, WebRequest request) {

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .message("Error de argumento ilegal")
                                .build();

                return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }

        // Excepción personalizada para "método o URL no encontrado"
        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<Object> handleNoHandlerFoundException(
                        NoHandlerFoundException ex, WebRequest request) {

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .message("Método o URL no encontrado")
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
                                .build();

                return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(
                        MethodArgumentTypeMismatchException ex, WebRequest request) {

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .message("Tipo de argumento no válido")
                                .build();

                return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(NoStockException.class)
        public ResponseEntity<Object> handleNoStockException(
                        NoStockException ex, WebRequest request) {

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .message("No hay stock disponible")
                                .build();

                return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }

        // Excepciones genéricas
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Object> handleAllExceptions(
                        Exception ex, WebRequest request) {

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .message("Error interno del servidor")
                                .build();

                return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);

        }

}