package com.juanma.proyecto_vn.Repositorys;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.juanma.proyecto_vn.models.Cart;
import com.juanma.proyecto_vn.models.ProductCart;

public interface ProductCartRepository extends JpaRepository<ProductCart, ProductCart.ProductCartPK> {
    List<ProductCart> findByCart(Cart cart);

    @Query("SELECT pc FROM ProductCart pc WHERE pc.product.id = :productId AND pc.cart.id = :cartId")
    ProductCart findByProductAndCart(UUID productId, UUID cartId);
}
