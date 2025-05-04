package com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para productos sin stock suficiente
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoStockException extends RuntimeException {

    public NoStockException(String message) {
        super(message);
    }
}
