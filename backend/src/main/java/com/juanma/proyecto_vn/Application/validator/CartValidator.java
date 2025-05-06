package com.juanma.proyecto_vn.Application.validator;

import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.Cart;
import com.juanma.proyecto_vn.domain.model.CartItem;
import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.NoStockException;

import java.util.ArrayList;
import java.util.List;

/**
 * Validador para operaciones relacionadas con el carrito de compras
 */
@Component
public class CartValidator {

    /**
     * Valida que haya stock suficiente para un producto
     * 
     * @param product  Producto a validar
     * @param quantity Cantidad solicitada
     * @throws IllegalArgumentException si el producto es nulo o la cantidad es
     *                                  inválida
     * @throws NoStockException         si no hay stock suficiente
     */
    public void validateStockAvailability(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (quantity > 100) {
            throw new IllegalArgumentException("Quantity cannot exceed 100 units per product");
        }
        if (product.getStock() < quantity) {
            throw new NoStockException("Insufficient stock for product: " + product.getName() +
                    " (requested: " + quantity + ", available: " + product.getStock() + ")");
        }
    }

    /**
     * Valida que un carrito sea válido
     * 
     * @param cart Carrito a validar
     * @throws IllegalArgumentException si el carrito es inválido
     */
    public void validateCart(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        if (cart.getUser() == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart items cannot be null or empty");
        }
        if (cart.getTotalPrice() < 0) {
            throw new IllegalArgumentException("Total price cannot be negative");
        }

        // Validar cada item del carrito
        for (CartItem item : cart.getItems()) {
            validateCartItem(item);
        }

        // Validar que el precio total coincida con la suma de los ítems
        double calculatedTotal = calculateCartTotal(cart);
        if (Math.abs(calculatedTotal - cart.getTotalPrice()) > 0.01) {
            throw new IllegalArgumentException("Cart total price does not match sum of items: calculated="
                    + calculatedTotal + ", provided=" + cart.getTotalPrice());
        }
    }

    /**
     * Valida un item del carrito
     * 
     * @param item Item a validar
     * @throws IllegalArgumentException si el item es inválido
     */
    private void validateCartItem(CartItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Cart item cannot be null");
        }
        if (item.getProduct() == null) {
            throw new IllegalArgumentException("Product in cart item cannot be null");
        }
        if (item.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero for product: "
                    + (item.getProduct().getName() != null ? item.getProduct().getName() : "unknown"));
        }
        if (item.getQuantity() > 100) {
            throw new IllegalArgumentException("Quantity cannot exceed 100 units for product: "
                    + (item.getProduct().getName() != null ? item.getProduct().getName() : "unknown"));
        }
        if (item.getProduct().getPrice() == null) {
            throw new IllegalArgumentException("Price cannot be null for product: "
                    + (item.getProduct().getName() != null ? item.getProduct().getName() : "unknown"));
        }
    }

    /**
     * Calcula el precio total de un carrito sumando cada ítem
     * 
     * @param cart Carrito a calcular
     * @return Precio total calculado
     */
    private double calculateCartTotal(Cart cart) {
        return cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    /**
     * Valida un carrito y devuelve una lista de errores en lugar de lanzar
     * excepciones
     * 
     * @param cart Carrito a validar
     * @return Lista de errores encontrados
     */
    public List<String> validateCartAndReturnErrors(Cart cart) {
        List<String> errors = new ArrayList<>();

        if (cart == null) {
            errors.add("Cart cannot be null");
            return errors;
        }

        if (cart.getUser() == null) {
            errors.add("User cannot be null");
        }

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            errors.add("Cart items cannot be null or empty");
            return errors;
        }

        if (cart.getTotalPrice() < 0) {
            errors.add("Total price cannot be negative");
        }

        // Validar cada item del carrito
        for (CartItem item : cart.getItems()) {
            if (item == null) {
                errors.add("Cart item cannot be null");
                continue;
            }

            if (item.getProduct() == null) {
                errors.add("Product in cart item cannot be null");
                continue;
            }

            if (item.getQuantity() <= 0) {
                errors.add("Quantity must be greater than zero for product: "
                        + (item.getProduct().getName() != null ? item.getProduct().getName() : "unknown"));
            }

            if (item.getQuantity() > 100) {
                errors.add("Quantity cannot exceed 100 units for product: "
                        + (item.getProduct().getName() != null ? item.getProduct().getName() : "unknown"));
            }

            if (item.getProduct().getStock() < item.getQuantity()) {
                errors.add("Insufficient stock for product: " + item.getProduct().getName() +
                        " (requested: " + item.getQuantity() + ", available: " + item.getProduct().getStock() + ")");
            }
        }

        // Validar que el precio total coincida con la suma de los ítems
        double calculatedTotal = calculateCartTotal(cart);
        if (Math.abs(calculatedTotal - cart.getTotalPrice()) > 0.01) {
            errors.add("Cart total price does not match sum of items: calculated="
                    + calculatedTotal + ", provided=" + cart.getTotalPrice());
        }

        return errors;
    }
}
