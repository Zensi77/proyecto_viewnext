package com.juanma.proyecto_vn.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private String id;
    private User user;
    private List<CartItem> items;
    private double totalPrice;

    public void addItem(CartItem item) {
        items.add(item);
        recalculateTotalPrice();
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        recalculateTotalPrice();
    }

    private void recalculateTotalPrice() {
        totalPrice = 0.0;
        for (CartItem item : items) {
            totalPrice += item.getProduct().getPrice() * item.getQuantity();
        }
    }
}
