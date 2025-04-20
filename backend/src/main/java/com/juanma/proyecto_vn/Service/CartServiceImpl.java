package com.juanma.proyecto_vn.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.juanma.proyecto_vn.Repositorys.CartRepository;
import com.juanma.proyecto_vn.interfaces.ICartService;
import com.juanma.proyecto_vn.models.Cart;
import com.juanma.proyecto_vn.models.ProductCart;

@Service
public class CartServiceImpl implements ICartService {

    @Override
    public List<ProductCart> getCartByUserId(String email) {
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