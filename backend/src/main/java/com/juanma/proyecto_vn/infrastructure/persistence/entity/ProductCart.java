package com.juanma.proyecto_vn.infrastructure.persistence.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Data
@EqualsAndHashCode(callSuper = true) // Para que no de error al hacer equals y hashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_cart")
public class ProductCart extends BaseEntity {

    @EmbeddedId
    private ProductCartPK id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("productId") // Mapea el campo productId de la clave compuesta
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("cartId") // Mapea el campo cartId de la clave compuesta
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(name = "quantity", nullable = false, columnDefinition = "int default 1")
    private int quantity;

    @Embeddable // Clase embebida para la clave primaria compuesta
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductCartPK implements Serializable { // Serializable es necesario para claves compuestas
        @Column(name = "product_id", nullable = false, columnDefinition = "char(36)")
        @JdbcTypeCode(SqlTypes.CHAR)
        private UUID productId;

        @Column(name = "cart_id", nullable = false, columnDefinition = "char(36)")
        @JdbcTypeCode(SqlTypes.CHAR)
        private UUID cartId;
    }
}