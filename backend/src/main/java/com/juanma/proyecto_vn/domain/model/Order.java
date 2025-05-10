package com.juanma.proyecto_vn.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.juanma.proyecto_vn.shared.Utils.enums.paymentMethodEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio para Orders (pedidos), independiente de la infraestructura
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private UUID id;
    private UUID userId;
    private List<OrderItem> items;
    private Double totalPrice;
    private String status = "PENDING";
    private paymentMethodEnum paymentMethod;
    private LocalDateTime createdAt = LocalDateTime.now();

    public void calculateTotalPrice() {
        this.totalPrice = items.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}
