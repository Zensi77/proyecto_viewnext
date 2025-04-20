package com.juanma.proyecto_vn.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper = true) // Para que no de error al hacer equals y hashCode
@SQLDelete(sql = "UPDATE product_order SET is_deleted = true WHERE product_id = ? AND order_id = ?")
@Filter(name = "deletedFilter", condition = "is_deleted = :isDeleted")
public class ProductOrder extends BaseEntity {

    @EmbeddedId
    private ProductOrderPK id;

    @MapsId("orderId")
    @ManyToOne(fetch = FetchType.LAZY) // Hace que no se cargue el producto al cargar la orden
    @JoinColumn(name = "product_id")
    private Product product;

    @MapsId("productId") // Mapea la clave foránea con la clave primaria compuesta
    @ManyToOne(fetch = FetchType.LAZY) // Hace que no se cargue la orden al cargar el producto
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted = false;

    @Embeddable // Clase embebida para la clave primaria compuesta
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductOrderPK implements Serializable { // Serializable es necesario para claves compuestas
        @Column(name = "product_id", nullable = false, columnDefinition = "char(36)")
        private UUID productId;

        @Column(name = "order_id", nullable = false, columnDefinition = "char(36)")
        private UUID orderId;
    }
}
