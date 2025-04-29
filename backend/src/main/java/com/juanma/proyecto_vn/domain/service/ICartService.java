package com.juanma.proyecto_vn.domain.service;

import java.util.UUID;

import com.juanma.proyecto_vn.interfaces.rest.dtos.cart.CartDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.cart.CreateProductCartDto;

public interface ICartService {
    CartDto getCartByUserId(String email);

    CartDto addProductToCart(CreateProductCartDto productCart, String email);

    void deleteProductFromCart(UUID productId, String email);
}
