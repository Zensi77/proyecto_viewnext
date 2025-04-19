package com.juanma.proyecto_vn.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

@Entity
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper = true) // Para que no de error al hacer equals y hashCode
@IdClass(ProductOrder.class)
@SQLDelete(sql = "UPDATE product_order SET is_deleted = true WHERE id = ?")
@Filter(name = "deletedFilter", condition = "is_deleted = :isDeleted")
public class ProductOrder extends BaseEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY) // Hace que no se cargue el producto al cargar la orden
    @JoinColumn(name = "product_id")
    private Product product;

    @Id
    @ManyToOne(fetch = FetchType.LAZY) // Hace que no se cargue la orden al cargar el producto
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
