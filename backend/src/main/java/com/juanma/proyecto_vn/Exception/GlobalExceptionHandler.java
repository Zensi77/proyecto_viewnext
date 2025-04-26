
package com.juanma.proyecto_vn.Exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.juanma.proyecto_vn.Exception.CustomExceptions.NoStockException;
import com.juanma.proyecto_vn.Exception.CustomExceptions.ResourceNotFoundException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gestión centralizada de excepciones para la API.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        private ResponseEntity<Object> buildResponse(ApiError err, Throwable ex) {
                if (err.getStatus() >= 500 && ex != null) {
                        Map<String, Object> details = new HashMap<>();
                        details.put("exception", ex.getClass().getName());
                        err.setDetails(details);
                }
                log.error("[{}] {}: {}", err.getStatus(), err.getPath(), err.getMessage(), ex);
                return ResponseEntity.status(err.getStatus()).body(err);
        }

        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {
                List<String> errs = ex.getFieldErrors().stream()
                                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                                .collect(Collectors.toList());

                ApiError apiErr = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(status.value())
                                .error("Validation Failed")
                                .message("One or more fields are invalid.")
                                .path(request.getDescription(false).substring(4))
                                .validationErrors(errs)
                                .build();
                return buildResponse(apiErr, ex);
        }

        @Override
        protected ResponseEntity<Object> handleHttpMessageNotReadable(
                        HttpMessageNotReadableException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {
                ApiError apiErr = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(status.value())
                                .error("Malformed JSON request")
                                .message(ex.getMostSpecificCause().getMessage())
                                .path(request.getDescription(false).substring(4))
                                .build();
                return buildResponse(apiErr, ex);
        }

        @Override
        protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
                        HttpRequestMethodNotSupportedException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {
                String allow = String.join(", ", ex.getSupportedHttpMethods().stream()
                                .map(HttpMethod::name)
                                .toList());
                ApiError apiErr = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(status.value())
                                .error("Method Not Allowed")
                                .message(ex.getMethod() + " not supported. Supported: " + allow)
                                .path(request.getDescription(false).substring(4))
                                .build();
                return buildResponse(apiErr, ex);
        }

        @Override
        protected ResponseEntity<Object> handleMissingServletRequestParameter(
                        MissingServletRequestParameterException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {
                ApiError apiErr = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(status.value())
                                .error("Missing Parameter")
                                .message("Parameter '" + ex.getParameterName() + "' is required")
                                .path(request.getDescription(false).substring(4))
                                .build();
                return buildResponse(apiErr, ex);
        }

        @Override
        protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
                        HttpMediaTypeNotSupportedException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {
                ApiError apiErr = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(status.value())
                                .error("Unsupported Media Type")
                                .message(ex.getContentType() + " not supported. Supported: "
                                                + ex.getSupportedMediaTypes())
                                .path(request.getDescription(false).substring(4))
                                .build();
                return buildResponse(apiErr, ex);
        }

        @Override
        protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
                        HttpMediaTypeNotAcceptableException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {
                ApiError apiErr = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(status.value())
                                .error("Not Acceptable")
                                .message("Acceptable types: " + ex.getSupportedMediaTypes())
                                .path(request.getDescription(false).substring(4))
                                .build();
                return buildResponse(apiErr, ex);
        }

        @Override
        protected ResponseEntity<Object> handleNoHandlerFoundException(
                        NoHandlerFoundException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {

                ApiError apiErr = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Not Found")
                                .message("Recurso no encontrado")
                                .path(request.getDescription(false).replace("uri=", ""))
                                .build();

                return buildResponse(apiErr, ex);
        }

        @ExceptionHandler({ ConstraintViolationException.class,
                        MethodArgumentTypeMismatchException.class })
        public ResponseEntity<Object> handleBadRequest(Exception ex, WebRequest req) {
                String msg;
                if (ex instanceof ConstraintViolationException cve) {
                        msg = cve.getConstraintViolations().stream()
                                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                        .collect(Collectors.joining("; "));
                } else {
                        msg = ex.getMessage();
                }
                ApiError apiErr = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Bad Request")
                                .message(msg)
                                .path(req.getDescription(false).substring(4))
                                .build();
                return buildResponse(apiErr, ex);
        }

        @ExceptionHandler({ DataIntegrityViolationException.class, SQLException.class })
        protected ResponseEntity<Object> handleConflict(Exception ex, WebRequest req) {
                String detail = Optional.ofNullable(ex.getMessage()).orElse("Database error");
                ApiError apiErr = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.CONFLICT.value())
                                .error("Conflict")
                                .message(detail.contains("Duplicate") ? "Duplicate entry" : "Database integrity error")
                                .path(req.getDescription(false).substring(4))
                                .build();
                return buildResponse(apiErr, ex);
        }

        @ExceptionHandler(NoStockException.class)
        protected ResponseEntity<Object> handleNoStock(NoStockException ex, WebRequest req) {
                ApiError apiErr = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("No Stock Available")
                                .message(ex.getMessage())
                                .path(req.getDescription(false).substring(4))
                                .build();
                return buildResponse(apiErr, ex);
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        protected ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex,
                        WebRequest req) {
                ApiError apiErr = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Resource Not Found")
                                .message(ex.getMessage())
                                .path(req.getDescription(false).substring(4))
                                .build();
                return buildResponse(apiErr, ex);
        }

        @ExceptionHandler(Exception.class)
        protected ResponseEntity<Object> handleAllUncaught(Exception ex, WebRequest req) {
                ApiError apiErr = ApiError.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error("Internal Server Error")
                                .message("Unexpected error occurred")
                                .path(req.getDescription(false).substring(4))
                                .build();
                return buildResponse(apiErr, ex);
        }
}
