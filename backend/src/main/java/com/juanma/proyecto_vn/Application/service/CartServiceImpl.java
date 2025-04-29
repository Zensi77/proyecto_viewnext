package com.juanma.proyecto_vn.Application.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.juanma.proyecto_vn.domain.service.ICartService;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.Cart;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.Category;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.Product;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductCart;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.Provider;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.User;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.CartRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.ProductCartRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.ProductRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.UserRepository;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.NoStockException;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;
import com.juanma.proyecto_vn.interfaces.rest.dtos.cart.CartDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.cart.CreateProductCartDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.cart.GetProductCartDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.category.CategoryDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.product.GetProductDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.provider.ProviderDto;

import jakarta.transaction.Transactional;

@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductCartRepository productCartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MetricsService producerService;

    @Override
    @Transactional
    @PreAuthorize("#email == authentication.principal.username")
    public CartDto getCartByUserId(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("El usuario no existe.");
        }

        Optional<Cart> cart = cartRepository.findByUserId(user.get().getId());

        if (user.isEmpty()) {
            throw new ResourceNotFoundException("El usuario no existe.");
        }

        if (cart.isEmpty()) {
            Cart newCart = Cart.builder()
                    .user(user.get())
                    .productCart(List.of())
                    .build();
            cartRepository.save(newCart);
            return convertToCartDto(newCart);
        }

        return convertToCartDto(cart.get());
    }

    @Override
    @Transactional
    @PreAuthorize("#email == authentication.principal.username")
    public CartDto addProductToCart(CreateProductCartDto productCart, String email) {

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("El usuario no existe.");
        }

        producerService.sendFunnelEvent("add_to_cart", user.get().getId().toString(), Map.of(
                "product_id", productCart.getProduct_id(),
                "quantity", productCart.getQuantity()));

        Optional<Cart> cart = cartRepository.findByUserId(user.get().getId());

        if (cart.isPresent()) {
            Cart existingCart = cart.get();

            boolean productExists = existingCart.getProductCart().stream()
                    .anyMatch(pc -> pc.getProduct().getId().toString().equals(productCart.getProduct_id().toString()));

            Product product = productRepository.findById(productCart.getProduct_id())
                    .orElseThrow(() -> new ResourceNotFoundException("El producto no existe."));

            int productStock = product.getStock();
            if (productExists) {
                int quantityInCart = productCartRepository
                        .findByProductAndCart(productCart.getProduct_id(), existingCart.getId()).get().getQuantity()
                        + 1;

                if (quantityInCart > productStock) {
                    throw new NoStockException("No hay suficiente stock del producto: ");
                }

                Optional<ProductCart> productCartDb = productCartRepository.findByProductAndCart(
                        productCart.getProduct_id(), existingCart.getId());

                if (productCartDb.isPresent()) {

                    productCartDb.get().setQuantity(productCartDb.get().getQuantity() + productCart.getQuantity());
                    productCartRepository.save(productCartDb.get());
                    updatePriceCard(existingCart);
                }

            } else {
                if (productCart.getQuantity() > productStock) {
                    throw new NoStockException("No hay suficiente stock del producto: ");
                }

                ProductCart.ProductCartPK pk = new ProductCart.ProductCartPK(
                        productCart.getProduct_id(),
                        existingCart.getId());

                existingCart.getProductCart().add(ProductCart.builder()
                        .id(pk)
                        .product(productRepository.findById(productCart.getProduct_id()).get())
                        .cart(existingCart)
                        .quantity(productCart.getQuantity())
                        .build());

                cartRepository.save(existingCart);
                updatePriceCard(existingCart);
            }

            return getCartByUserId(email);
        } else {
            getCartByUserId(email);
            throw new ResourceNotFoundException("El carrito no existe, reintentelo mas tarde.");
        }
    }

    @Override
    @Transactional
    @PreAuthorize("#email == authentication.principal.username")
    public void deleteProductFromCart(UUID productId, String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("El usuario no existe.");
        }

        Optional<Cart> cart = cartRepository.findByUserId(user.get().getId());

        if (cart.isEmpty()) {
            throw new ResourceNotFoundException("El carrito no existe.");
        }

        Optional<ProductCart> productCart = productCartRepository.findByProductAndCart(productId, cart.get().getId());
        if (productCart.isEmpty()) {
            throw new ResourceNotFoundException("El producto no existe en el carrito.");
        }

        productCartRepository.deleteByProductAndCart(productId, cart.get().getId());

    }

    private CartDto convertToCartDto(Cart cart) {
        List<GetProductCartDto> productCartDtos = cart.getProductCart().stream()
                .map(productCart -> {
                    if (productCart.getProduct() == null) {
                        productCartRepository.deleteByProductAndCart(null, cart.getId());
                        return null;

                    }
                    return GetProductCartDto.builder()
                            .product(getProdutDto(productCart))
                            .quantity(productCart.getQuantity())
                            .build();
                })
                .filter(dto -> dto != null)
                .toList();

        return CartDto.builder()
                .cart_id(cart.getId().toString())
                .products(productCartDtos)
                .build();
    }

    private void updatePriceCard(Cart cart) {
        List<ProductCart> productCarts = cart.getProductCart();

        Double totalPrice = productCarts.stream()
                .mapToDouble(productCart -> productCart.getProduct().getPrice() * productCart.getQuantity())
                .sum();

        cart.setTotal_price(totalPrice);

        cartRepository.save(cart);
    }

    private CategoryDto getCategoryDtoFromProduct(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    private ProviderDto getCategoryDtoFromProduct(Provider provider) {
        return ProviderDto.builder()
                .id(provider.getId())
                .name(provider.getName())
                .address(provider.getAddress())
                .build();
    }

    private GetProductDto getProdutDto(ProductCart productCart) {
        return GetProductDto.builder()
                .id(productCart.getProduct().getId())
                .name(productCart.getProduct().getName())
                .price(productCart.getProduct().getPrice())
                .image(productCart.getProduct().getImage())
                .description(productCart.getProduct().getDescription())
                .stock(productCart.getProduct().getStock())
                .category(
                        getCategoryDtoFromProduct(productCart.getProduct().getCategory()))
                .provider(
                        getCategoryDtoFromProduct(productCart.getProduct()
                                .getProvider()))
                .build();
    }

}