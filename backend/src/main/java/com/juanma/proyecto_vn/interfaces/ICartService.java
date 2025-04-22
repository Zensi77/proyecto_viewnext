package com.juanma.proyecto_vn.interfaces;

import java.util.UUID;

import com.juanma.proyecto_vn.Dtos.Cart.CartDto;
import com.juanma.proyecto_vn.Dtos.Cart.CreateProductCartDto;

public interface ICartService {
    CartDto getCartByUserId(String email);

    CartDto addProductToCart(CreateProductCartDto productCart, String email);

    void deleteProductFromCart(UUID productId, UUID cartId, String email);
}
