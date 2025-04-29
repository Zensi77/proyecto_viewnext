package com.juanma.proyecto_vn.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.Cart;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductCart;


public interface ProductCartRepository extends JpaRepository<ProductCart, ProductCart.ProductCartPK> {
    List<ProductCart> findByCart(Cart cart);

    @Query("SELECT pc FROM ProductCart pc WHERE pc.product.id = :productId AND pc.cart.id = :cartId")
    Optional<ProductCart> findByProductAndCart(UUID productId, UUID cartId);

    @Modifying
    @Query("DELETE FROM ProductCart pc WHERE pc.id.productId = :productId AND pc.id.cartId = :cartId")
    void deleteByProductAndCart(UUID productId, UUID cartId); // Cambia el nombre del método para que sea más claro

    @Modifying
    @Query("DELETE FROM ProductCart pc WHERE pc.id.productId = :productId")
    void deleteByProduct(UUID productId);
}
