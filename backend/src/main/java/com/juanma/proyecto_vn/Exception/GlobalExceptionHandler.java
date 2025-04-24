package com.juanma.proyecto_vn.Exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.infinispan.commons.CacheException;
import org.infinispan.protostream.annotations.ProtoSchemaBuilderException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationNotSupportedException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Centralizada gestión de errores para la API REST.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        private ResponseEntity<Object> buildResponse(ApiError error) {
                // Agregar detalles adicionales para modo desarrollo
                if (error.getStatus() >= 500) {
                        // Solo agregar la causa para errores de servidor
                        Throwable cause = error.getCause();
                        if (cause != null) {
                                Map<String, Object> errorDetails = new HashMap<>();
                                errorDetails.put("Error Class", cause.getClass().getName());
                                errorDetails.put("error", cause.getMessage());
                                error.setErrors(errorDetails);
                        }
                }
                return ResponseEntity.status(error.getStatus()).body(error);
        }

        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {
                List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                                .collect(Collectors.toList());
                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(status.value())
                                .message("Error de validación")
                                .errors(Map.of("validationErrors", errors))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return buildResponse(apiError);
        }

        @Override
        protected ResponseEntity<Object> handleHttpMessageNotReadable(
                        HttpMessageNotReadableException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {
                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Error de formato")
                                .errors(Map.of("error", ex.getMessage()))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return buildResponse(apiError);
        }

        @Override
        protected ResponseEntity<Object> handleMissingServletRequestParameter(
                        MissingServletRequestParameterException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {
                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Parámetro requerido no proporcionado")
                                .errors(Map.of("error", "El parámetro '" + ex.getParameterName() + "' es obligatorio"))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return buildResponse(apiError);
        }

        // ============ EXCEPCIONES PERSONALIZADAS ============

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<Object> handleResourceNotFoundException(
                        ResourceNotFoundException ex, WebRequest request) {
                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .message("Recurso no encontrado")
                                .errors(Map.of("error", ex.getMessage()))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return buildResponse(apiError);
        }

        @ExceptionHandler(NoStockException.class)
        public ResponseEntity<Object> handleNoStockException(
                        NoStockException ex, WebRequest request) {
                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .message("Error de stock")
                                .errors(Map.of("error", ex.getMessage()))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return buildResponse(apiError);
        }

        @ExceptionHandler(ProtoSchemaBuilderException.class)
        protected ResponseEntity<Object> handleProtoSchemaBuilder(
                        ProtoSchemaBuilderException ex,
                        WebRequest request) {
                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .message("Error de formato")
                                .errors(Map.of("error", ex.getMessage()))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .cause(ex.getCause())
                                .build();
                return buildResponse(apiError);
        }

        @ExceptionHandler(CacheException.class)
        protected ResponseEntity<Object> handleCacheException(
                        CacheException ex,
                        WebRequest request) {
                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                                .message("Error de caché")
                                .errors(Map.of("error", "Error al acceder a la caché: " + ex.getMessage()))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .cause(ex.getCause())
                                .build();
                return buildResponse(apiError);
        }

        @ExceptionHandler({ QueryTimeoutException.class, org.springframework.dao.QueryTimeoutException.class })
        protected ResponseEntity<Object> handleQueryTimeout(
                        Exception ex,
                        WebRequest request) {
                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.REQUEST_TIMEOUT.value())
                                .message("Tiempo de espera agotado")
                                .errors(Map.of("error",
                                                "La operación de base de datos ha excedido el tiempo máximo permitido."))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .cause(ex.getCause())
                                .build();
                return buildResponse(apiError);
        }

        @ExceptionHandler(NoSuchElementException.class)
        protected ResponseEntity<Object> handleNoSuchElement(
                        NoSuchElementException ex,
                        WebRequest request) {
                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .message("Recurso no encontrado")
                                .errors(Map.of("error", "El recurso solicitado no existe: " + ex.getMessage()))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return buildResponse(apiError);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        protected ResponseEntity<Object> handleIllegalArgument(
                        IllegalArgumentException ex,
                        WebRequest request) {
                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Argumento inválido")
                                .errors(Map.of("error", ex.getMessage()))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return buildResponse(apiError);
        }

        @ExceptionHandler(MissingRequestHeaderException.class)
        protected ResponseEntity<Object> handleMissingHeader(
                        MissingRequestHeaderException ex,
                        WebRequest request) {
                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Encabezado faltante")
                                .errors(Map.of("error", "El encabezado " + ex.getHeaderName() + " es obligatorio"))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return buildResponse(apiError);
        }

        @ExceptionHandler({ SQLException.class })
        protected ResponseEntity<Object> handleSQLException(
                        SQLException ex,
                        WebRequest request) {
                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .message("Error de base de datos")
                                .errors(Map.of("error", "Ha ocurrido un error en la operación de base de datos"))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .cause(ex)
                                .build();
                return buildResponse(apiError);
        }

        @ExceptionHandler({ IOException.class })
        protected ResponseEntity<Object> handleIOException(
                        IOException ex,
                        WebRequest request) {
                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .message("Error de E/S")
                                .errors(Map.of("error", "Error al leer o escribir datos: " + ex.getMessage()))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return buildResponse(apiError);
        }

        // Excepciones de autenticación
        @ExceptionHandler({
                        BadCredentialsException.class,
                        UsernameNotFoundException.class,
                        DisabledException.class,
                        LockedException.class,
                        AuthenticationNotSupportedException.class,
                        AuthenticationException.class
        })
        protected ResponseEntity<Object> handleAuthenticationException(
                        Exception ex,
                        WebRequest request) {

                HttpStatus status = HttpStatus.UNAUTHORIZED;
                String error = "Error de autenticación";
                String message = ex.getMessage();

                if (ex instanceof DisabledException) {
                        message = "Cuenta deshabilitada";
                } else if (ex instanceof LockedException) {
                        message = "Cuenta bloqueada";
                } else if (ex instanceof BadCredentialsException || ex instanceof UsernameNotFoundException) {
                        message = "Credenciales inválidas";
                }

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(status.value())
                                .message(error)
                                .errors(Map.of("error", message))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return buildResponse(apiError);
        }

        @ExceptionHandler(AccessDeniedException.class)
        protected ResponseEntity<Object> handleAccessDenied(
                        AccessDeniedException ex,
                        WebRequest request) {
                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.FORBIDDEN.value())
                                .message("Acceso denegado")
                                .errors(Map.of("error", "No tiene permisos para acceder a este recurso"))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return buildResponse(apiError);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        protected ResponseEntity<Object> handleDataIntegrityViolation(
                        DataIntegrityViolationException ex,
                        WebRequest request) {
                String message = "Error de integridad de datos";
                // Verificar si es por duplicidad
                if (ex.getCause() != null && ex.getCause().getMessage().contains("Duplicate entry")) {
                        message = "Ya existe un registro con esos datos";
                }

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .message(message)
                                .errors(Map.of("error", ex.getMessage()))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return buildResponse(apiError);
        }

        // Excepciones de Constraint Validation
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<Object> handleConstraintViolation(
                        ConstraintViolationException ex,
                        WebRequest request) {
                List<String> errors = ex.getConstraintViolations()
                                .stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.toList());

                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Error de validación")
                                .errors(Map.of("validationErrors", errors))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();
                return buildResponse(apiError);
        }

        // Excepciones genéricas como último recurso
        @ExceptionHandler(Exception.class)
        protected ResponseEntity<Object> handleAll(
                        Exception ex,
                        WebRequest request) {
                ApiError apiError = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .message("Error interno del servidor")
                                .errors(Map.of("error", ex.getMessage()))
                                .path(request.getDescription(false).replace("uri=", ""))
                                .cause(ex)
                                .build();
                return buildResponse(apiError);
        }
}