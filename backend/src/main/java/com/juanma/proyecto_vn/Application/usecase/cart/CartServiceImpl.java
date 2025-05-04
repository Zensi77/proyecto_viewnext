package com.juanma.proyecto_vn.Application.usecase.cart;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.juanma.proyecto_vn.Application.validator.CartValidator;
import com.juanma.proyecto_vn.domain.model.Cart;
import com.juanma.proyecto_vn.domain.model.CartItem;
import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.domain.model.User;
import com.juanma.proyecto_vn.domain.repository.CartRepository;
import com.juanma.proyecto_vn.domain.repository.ProductCartRepository;
import com.juanma.proyecto_vn.domain.repository.ProductRepository;
import com.juanma.proyecto_vn.domain.repository.UserRepository;
import com.juanma.proyecto_vn.domain.service.ICartService;
import com.juanma.proyecto_vn.domain.service.IMetricsService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {
    private final CartRepository cartRepository;
    private final ProductCartRepository productCartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final IMetricsService metricService;
    private final CartValidator cartValidator;

    @Override
    @PreAuthorize("#email == authentication.principal.username")
    public Cart getCartByUserId(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("El usuario no existe.");
        }

        Cart cart = cartRepository.findByUserId(user.get().getId().toString());

        if (cart == null) {
            Cart newCart = Cart.builder()
                    .user(user.get())
                    .items(List.of())
                    .totalPrice(0.0)
                    .build();
            return cartRepository.save(newCart);
        }

        return cart;
    }

    @Override
    @PreAuthorize("#email == authentication.principal.username")
    public Cart addProductToCart(CartItem productCart, String email) {

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("El usuario no existe.");
        }

        metricService.sendFunnelEvent("add_to_cart", user.get().getId().toString(), Map.of(
                "product_id", productCart.getProduct().getId().toString(),
                "quantity", productCart.getQuantity()));

        Cart cart = cartRepository.findByUserId(user.get().getId().toString());

        if (cart == null) {
            Cart newCart = Cart.builder()
                    .user(user.get())
                    .items(List.of())
                    .totalPrice(0.0)
                    .build();
            cart = cartRepository.save(newCart);
        }

        boolean productExists = cart.getItems().stream()
                .anyMatch(pc -> pc.getProduct().getId().toString().equals(productCart.getProduct().getId()
                        .toString()));

        Product product = productRepository.findById(productCart.getProduct().getId());

        if (productExists) {
            cartValidator.validateStockAvailability(product, productCart.getQuantity());

            CartItem productCartDb = productCartRepository.findByProductAndCart(
                    productCart.getProduct().getId(), UUID.fromString(cart.getId()));

            productCartDb.setQuantity(productCart.getQuantity() + productCartDb.getQuantity());
            productCartRepository.save(productCartDb);


        } else {
            cartValidator.validateStockAvailability(product, productCart.getQuantity());

            cart.addItem(CartItem.builder()
                    .product(product)
                    .cart(cart)
                    .quantity(productCart.getQuantity())
                    .build());
            cartRepository.save(cart);
        }
        
        return cartRepository.findByUserId(user.get().getId().toString());
    }

    @Override
    @PreAuthorize("#email == authentication.principal.username")
    public void deleteProductFromCart(UUID productId, String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("El usuario no existe.");
        }

        metricService.sendFunnelEvent("remove_from_cart", user.get().getId().toString(), Map.of(
                "product_id", productId));

        Cart cart = cartRepository.findByUserId(user.get().getId().toString());

        productCartRepository.findByProductAndCart(productId,
                UUID.fromString(cart.getId()));

        productCartRepository.deleteByProductAndCart(productId, UUID.fromString(cart.getId()));

        cart.removeItem(CartItem.builder()
                .product(Product.builder()
                        .id(productId)
                        .build())
                .cart(cart)
                .build());
        cartRepository.save(cart);
    }

}