package com.juanma.proyecto_vn.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "product_order")
@EqualsAndHashCode(callSuper = true) // Para que no de error al hacer equals y hashCode
@ToString(callSuper = true) // Para que no de error al hacer toString
public class ProductOrderEntity extends BaseEntity {

    @EmbeddedId
    private ProductOrderPK id;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.EAGER) // Hace que no se cargue el producto al cargar la orden
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @MapsId("orderId") // Mapea la clave foránea con la clave primaria compuesta
    @ManyToOne(fetch = FetchType.EAGER) // Hace que no se cargue la orden al cargar el producto
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Embeddable // Clase embebida para la clave primaria compuesta
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductOrderPK implements Serializable { // Serializable es necesario para claves compuestas
        @Column(name = "product_id", nullable = false, columnDefinition = "char(36)")
        @JdbcTypeCode(SqlTypes.CHAR)
        private UUID productId;

        @Column(name = "order_id", nullable = false, columnDefinition = "char(36)")
        @JdbcTypeCode(SqlTypes.CHAR)
        private UUID orderId;
    }
}
