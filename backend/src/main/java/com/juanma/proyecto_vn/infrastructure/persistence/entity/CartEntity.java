package com.juanma.proyecto_vn.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad JPA para persistir carritos en la base de datos
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true) // Para que no de error al hacer equals y hashCode
@Table(name = "cart")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "char(36)")
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Builder.Default
    private List<ProductCartEntity> productCart = new ArrayList<>();

    @Column(name = "total_price", nullable = false)
    @Builder.Default
    private Double total_price = 0.0;

    /**
     * Método para añadir un ProductCartEntity al carrito
     * 
     * @param productCartEntity La entidad ProductCartEntity a añadir
     */
    public void addProductCart(ProductCartEntity productCartEntity) {
        if (productCart == null) {
            productCart = new ArrayList<>();
        }

        productCart.add(productCartEntity);
        productCartEntity.setCart(this);
    }

    /**
     * Método para eliminar un ProductCartEntity del carrito
     * 
     * @param productCartEntity La entidad ProductCartEntity a eliminar
     */
    public void removeProductCart(ProductCartEntity productCartEntity) {
        if (productCart != null) {
            productCart.remove(productCartEntity);
            productCartEntity.setCart(null);
        }
    }
}
