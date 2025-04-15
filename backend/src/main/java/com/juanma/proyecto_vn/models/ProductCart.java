package com.juanma.proyecto_vn.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ProductCart.class)
@SQLDelete(sql = "UPDATE product_cart SET is_deleted = true WHERE id = ?")
@Filter(name = "deletedFilter", condition = "is_deleted = :isDeleted")
public class ProductCart extends BaseEntity{
    @Id
    @ManyToOne(fetch = FetchType.LAZY) // Hace que no se cargue el producto al cargar el carrito
    @JoinColumn(name = "product_id")
    private Product product;

    @Id
    @ManyToOne(fetch = FetchType.LAZY) // Hace que no se cargue el carrito al cargar el producto
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(name = "quantity", nullable = false, columnDefinition = "int default 1")
    private int quantity;
}
