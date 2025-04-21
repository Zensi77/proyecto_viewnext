package com.juanma.proyecto_vn.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.juanma.proyecto_vn.Repositorys.CartRepository;
import com.juanma.proyecto_vn.Repositorys.UserRepository;
import com.juanma.proyecto_vn.interfaces.ICartService;
import com.juanma.proyecto_vn.models.Cart;
import com.juanma.proyecto_vn.models.ProductCart;
import com.juanma.proyecto_vn.models.User;

@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ProductCart> getCartByUserId(String email) {
        User user = userRepository.findByEmail(email);

        Optional<Cart> cart = cartRepository.findByUserId(user.getId());

        if (cart.isPresent()) {
            return cart.get().getProductCart();
        } else {
            Cart newCart = Cart.builder().user(user).build();
            cartRepository.save(newCart);
        }

        return cart.get().getProductCart();
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