package com.juanma.proyecto_vn.domain.repository;

import java.util.UUID;

import com.juanma.proyecto_vn.domain.model.CartItem;

// todo borrar este archivo y el mapper de product cart
public interface ProductCartRepository {
    CartItem save(CartItem cartItem);

    CartItem findByProductAndCart(UUID productId, UUID cartId);

    void deleteByProductAndCart(UUID productId, UUID cartId);
}
