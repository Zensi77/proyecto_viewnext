package com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions;

/**
 * Excepción lanzada cuando no hay suficiente stock de un producto
 */
public class NoStockException extends RuntimeException {
    public NoStockException(String message) {
        super(message);
    }
}
