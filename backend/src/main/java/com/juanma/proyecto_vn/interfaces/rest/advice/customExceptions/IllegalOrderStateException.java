package com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para operaciones no permitidas en el estado actual de un pedido
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalOrderStateException extends RuntimeException {

    public IllegalOrderStateException(String message) {
        super(message);
    }
}