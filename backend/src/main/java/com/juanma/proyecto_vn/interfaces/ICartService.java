package com.juanma.proyecto_vn.interfaces;

import java.util.List;
import java.util.UUID;

import com.juanma.proyecto_vn.models.ProductCart;

public interface ICartService {
    List<ProductCart> getCartByUserId(String email);

    ProductCart addProductToCart(ProductCart productCart);

    ProductCart updateProductInCart(ProductCart productCart);

    void deleteProductFromCart(UUID productId, UUID cartId);
}
