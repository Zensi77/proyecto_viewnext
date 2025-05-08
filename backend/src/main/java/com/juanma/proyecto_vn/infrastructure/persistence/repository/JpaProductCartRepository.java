package com.juanma.proyecto_vn.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.CartEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductCartEntity;

public interface JpaProductCartRepository extends JpaRepository<ProductCartEntity, UUID> {
    List<ProductCartEntity> findByCart(CartEntity cart);

    @Query("SELECT pc FROM ProductCartEntity pc WHERE pc.product.id = :productId AND pc.cart.id = :cartId")
    Optional<ProductCartEntity> findByProductAndCart(UUID productId, UUID cartId);

    @Modifying
    @Query("DELETE FROM ProductCartEntity pc WHERE pc.cart.id = :cartId AND pc.product.id = :productId")
    void deleteByProductAndCart(UUID productId, UUID cartId); // Cambia el nombre del método para que sea más claro

    @Modifying
    @Query("DELETE FROM ProductCartEntity pc WHERE pc.cart.id = :cartId")
    void deleteByProduct(UUID productId);
}
