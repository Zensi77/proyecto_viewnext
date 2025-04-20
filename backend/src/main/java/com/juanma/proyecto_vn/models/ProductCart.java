package com.juanma.proyecto_vn.models;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_cart")
@SQLDelete(sql = "UPDATE product_cart SET is_deleted = true WHERE product_id = ? AND cart_id = ?")
@Filter(name = "deletedFilter", condition = "is_deleted = :isDeleted")
public class ProductCart {

    @EmbeddedId
    private ProductCartPK id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId") // Mapea el campo productId de la clave compuesta
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartId") // Mapea el campo cartId de la clave compuesta
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(name = "quantity", nullable = false, columnDefinition = "int default 1")
    private int quantity;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted = false;

    @Embeddable // Clase embebida para la clave primaria compuesta
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductCartPK implements Serializable { // Serializable es necesario para claves compuestas
        @Column(name = "product_id", nullable = false, columnDefinition = "char(36)")
        private UUID productId;

        @Column(name = "cart_id", nullable = false, columnDefinition = "char(36)")
        private UUID cartId;
    }
}