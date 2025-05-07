package com.juanma.proyecto_vn.domain.service;

import java.util.UUID;

import com.juanma.proyecto_vn.domain.model.Cart;
import com.juanma.proyecto_vn.domain.model.CartItem;

public interface ICartService {
    Cart getCartByUserId(String email);

    Cart addProductToCart(CartItem productCart, String email);

    Cart updateProductInCart(CartItem productCart, String email);

    void deleteProductFromCart(UUID productId, String email);

}
