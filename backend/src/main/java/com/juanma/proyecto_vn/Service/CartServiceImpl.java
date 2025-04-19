public
package com.juanma.proyecto_vn.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.juanma.proyecto_vn.Repositorys.CartRepository;
import com.juanma.proyecto_vn.interfaces.ICartService;
import com.juanma.proyecto_vn.models.ProductCart;

class CartServiceImpl implements ICartService {
    @Autowired
    private CartRepository cartRepository;

    @Override
    public List<ProductCart> getCartByUserId(UUID userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCartByUserId'");
    }

    @Override
    public ProductCart addProductToCart(ProductCart productCart) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addProductToCart'");
    }

    @Override
    public ProductCart updateProductInCart(ProductCart productCart) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateProductInCart'");
    }

    @Override
    public void deleteProductFromCart(UUID productId, UUID cartId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteProductFromCart'");
    }

}