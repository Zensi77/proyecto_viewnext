package com.juanma.proyecto_vn.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper = true) // Para que no de error al hacer equals y hashCode
public class ProductOrder extends BaseEntity {

    @EmbeddedId
    private ProductOrderPK id;

    @MapsId("orderId")
    @ManyToOne(fetch = FetchType.EAGER) // Hace que no se cargue el producto al cargar la orden
    @JoinColumn(name = "product_id")
    private Product product;

    @MapsId("productId") // Mapea la clave foránea con la clave primaria compuesta
    @ManyToOne(fetch = FetchType.EAGER) // Hace que no se cargue la orden al cargar el producto
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Embeddable // Clase embebida para la clave primaria compuesta
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductOrderPK implements Serializable { // Serializable es necesario para claves compuestas
        @Column(name = "product_id", nullable = false, columnDefinition = "char(36)")
        @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
        private UUID productId;

        @Column(name = "order_id", nullable = false, columnDefinition = "char(36)")
        @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
        private UUID orderId;
    }
}
