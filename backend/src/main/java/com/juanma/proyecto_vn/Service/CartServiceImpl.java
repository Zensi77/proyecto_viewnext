package com.juanma.proyecto_vn.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.juanma.proyecto_vn.Dtos.Cart.CartDto;
import com.juanma.proyecto_vn.Dtos.Cart.CreateProductCartDto;
import com.juanma.proyecto_vn.Dtos.Cart.GetProductCartDto;
import com.juanma.proyecto_vn.Dtos.Category.CategoryDto;
import com.juanma.proyecto_vn.Dtos.Product.GetProductDto;
import com.juanma.proyecto_vn.Dtos.Provider.ProviderDto;
import com.juanma.proyecto_vn.Exception.CustomExceptions.NoStockException;
import com.juanma.proyecto_vn.Exception.CustomExceptions.ResourceNotFoundException;
import com.juanma.proyecto_vn.Repositorys.CartRepository;
import com.juanma.proyecto_vn.Repositorys.ProductCartRepository;
import com.juanma.proyecto_vn.Repositorys.ProductRepository;
import com.juanma.proyecto_vn.Repositorys.UserRepository;
import com.juanma.proyecto_vn.interfaces.ICartService;
import com.juanma.proyecto_vn.models.Cart;
import com.juanma.proyecto_vn.models.Category;
import com.juanma.proyecto_vn.models.Product;
import com.juanma.proyecto_vn.models.ProductCart;
import com.juanma.proyecto_vn.models.Provider;
import com.juanma.proyecto_vn.models.User;

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
            }

            return getCartByUserId(email);
        } else {
            getCartByUserId(email);
            throw new ResourceNotFoundException("El carrito no existe, reintentelo mas tarde.");
        }
    }

    @Override
    @Transactional
    public void deleteProductFromCart(UUID productId, UUID cartId, String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("El usuario no existe.");
        }

        Optional<Cart> cart = cartRepository.findByUserId(user.get().getId());

        if (cart.isPresent() && cart.get().getId().equals(cartId)) {
            Optional<ProductCart> productCart = productCartRepository.findByProductAndCart(productId, cartId);

            if (productCart.isPresent()) {
                productCartRepository.deleteByProductAndCart(productId, cartId);
            }
        } else {
            throw new ResourceNotFoundException("El carrito no pertenece al usuario o no existe.");
        }
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