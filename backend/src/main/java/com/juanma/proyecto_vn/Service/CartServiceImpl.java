package com.juanma.proyecto_vn.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.juanma.proyecto_vn.Dtos.Cart.CartDto;
import com.juanma.proyecto_vn.Dtos.Cart.CreateProductCartDto;
import com.juanma.proyecto_vn.Dtos.Cart.GetProductCartDto;
import com.juanma.proyecto_vn.Dtos.Product.GetProductDto;
import com.juanma.proyecto_vn.Exception.NoStockException;
import com.juanma.proyecto_vn.Exception.ResourceNotFoundException;
import com.juanma.proyecto_vn.Repositorys.CartRepository;
import com.juanma.proyecto_vn.Repositorys.ProductCartRepository;
import com.juanma.proyecto_vn.Repositorys.ProductRepository;
import com.juanma.proyecto_vn.Repositorys.UserRepository;
import com.juanma.proyecto_vn.interfaces.ICartService;
import com.juanma.proyecto_vn.models.Cart;
import com.juanma.proyecto_vn.models.Product;
import com.juanma.proyecto_vn.models.ProductCart;
import com.juanma.proyecto_vn.models.User;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductCartRepository productCartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public CartDto getCartByUserId(String email) {
        User user = userRepository.findByEmail(email);

        Optional<Cart> cart = cartRepository.findByUserId(user.getId());

        if (cart.isEmpty()) {
            Cart newCart = Cart.builder().user(user).build();
            cartRepository.save(newCart);
        }

        return convertToCartDto(cart.get());
    }

    @Override
    public CartDto addProductToCart(CreateProductCartDto productCart, String email) {
        User user = userRepository.findByEmail(email);

        Optional<Cart> cart = cartRepository.findByUserId(user.getId());

        if (cart.isPresent()) {
            Cart existingCart = cart.get();

            boolean productExists = existingCart.getProductCart().stream()
                    .anyMatch(pc -> pc.getProduct().getId().toString().equals(productCart.getProduct_id().toString()));

            Product product = productRepository.findById(productCart.getProduct_id())
                    .orElseThrow(() -> new ResourceNotFoundException("El producto no existe."));

            int productStock = product.getStock();
            int quantityInCart = productCartRepository
                    .findByProductAndCart(productCart.getProduct_id(), existingCart.getId()).getQuantity() + 1;

            if (quantityInCart > productStock) {
                throw new NoStockException("No hay suficiente stock del producto: ");
            }

            if (productExists) {
                ProductCart productCartDb = productCartRepository.findByProductAndCart(
                        productCart.getProduct_id(), existingCart.getId());

                productCartDb.setQuantity(productCartDb.getQuantity() + productCart.getQuantity());
                productCartRepository.save(productCartDb);
            } else {
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
    public void deleteProductFromCart(UUID productId, UUID cartId, String email) {
        User user = userRepository.findByEmail(email);
        Optional<Cart> cart = cartRepository.findByUserId(user.getId());

        if (cart.isPresent() && cart.get().getId().equals(cartId)) {
            ProductCart.ProductCartPK pk = new ProductCart.ProductCartPK(productId, cartId);
            ProductCart productCart = productCartRepository.findById(pk)
                    .orElseThrow(() -> new ResourceNotFoundException("El producto no existe en el carrito."));

            if (productCart != null) {
                productCartRepository.delete(productCart);
            } else {
                throw new ResourceNotFoundException("El producto no existe en el carrito.");
            }
        } else {
            throw new ResourceNotFoundException("El carrito no existe, reintentelo mas tarde.");
        }
    }

    private CartDto convertToCartDto(Cart cart) {
        List<GetProductCartDto> productCartDtos = cart.getProductCart().stream()
                .map(productCart -> GetProductCartDto.builder()
                        .product(GetProductDto.builder()
                                .id(productCart.getProduct().getId())
                                .name(productCart.getProduct().getName())
                                .price(productCart.getProduct().getPrice())
                                .image(productCart.getProduct().getImage())
                                .description(productCart.getProduct().getDescription())
                                .stock(productCart.getProduct().getStock())
                                .category(productCart.getProduct().getCategory().getName())
                                .provider(productCart.getProduct().getProvider().getName())
                                .build())
                        .quantity(productCart.getQuantity())
                        .build())
                .toList();

        return CartDto.builder()
                .cart_id(cart.getId().toString())
                .products(productCartDtos)
                .build();
    }

}