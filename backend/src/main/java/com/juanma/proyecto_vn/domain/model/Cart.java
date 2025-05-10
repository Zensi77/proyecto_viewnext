package com.juanma.proyecto_vn.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de dominio para el carrito de compras
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private String id;
    private User user;

    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    private Double totalPrice;

    /**
     * Añade un item al carrito
     * 
     * @param item Item a añadir
     */
    public void addItem(CartItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }

        if (item != null) {
            item.setCart(this);
            this.items.add(item);
            recalculateTotalPrice();
        }
    }

    public void cleanCart() {
        if (this.items != null) {
            this.items.clear();
            this.totalPrice = 0.0;
        }
    }

    /**
     * Elimina un item del carrito
     * 
     * @param productId ID del producto a eliminar
     */
    public void removeItem(UUID productId) {
        if (this.items != null) {
            this.items.removeIf(item -> Objects.equals(item.getProduct().getId(), productId));

            recalculateTotalPrice();
        }
    }

    public void updateQuantity(UUID productId, int quantity) {
        if (this.items != null) {
            for (CartItem item : this.items) {
                if (Objects.equals(item.getProduct().getId(), productId)) {
                    item.setQuantity(quantity);
                    break;
                }
            }

            recalculateTotalPrice();
        }
    }

    /**
     * Recalcula el precio total del carrito
     */
    public void recalculateTotalPrice() {
        this.totalPrice = 0.0;

        if (items == null || items.isEmpty()) {
            return;
        }

        for (CartItem item : items) {
            if (item == null || item.getProduct() == null || item.getProduct().getPrice() == null) {
                continue;
            }
            this.totalPrice += item.getProduct().getPrice() * item.getQuantity();
        }
    }
}
