package com.juanma.proyecto_vn.Application.usecase.cart;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación del servicio de carrito de compras.
 * Esta clase maneja la lógica de negocio relacionada con el carrito de compras,
 * incluyendo la adición y eliminación de productos, así como la obtención del
 * carrito del usuario.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements ICartService {
    private final CartRepository cartRepository;
    private final ProductCartRepository productCartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartValidator cartValidator;
    private final IMetricsService metricsService;

    /**
     * Obtiene el carrito de un usuario por su email.
     * Si el usuario no tiene un carrito, crea uno nuevo.
     * 
     * @param email El email del usuario.
     * @return El carrito del usuario.
     */
    @Override
    @PreAuthorize("#email == authentication.principal.username")
    public Cart getCartByUserId(String email) {
        log.debug("Obteniendo carrito para el usuario con email: {}", email);

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.warn("Usuario no encontrado con email: {}", email);
            throw new UsernameNotFoundException("El usuario no existe.");
        }

        Cart cart = cartRepository.findByUserId(user.get().getId());

        if (cart == null) {
            log.debug("Carrito no encontrado para el usuario. Creando nuevo carrito.");

            Cart newCart = Cart.builder()
                    .user(user.get())
                    .items(new ArrayList<>())
                    .totalPrice(0.0)
                    .build();

            cart = cartRepository.save(newCart);
            log.info("Nuevo carrito creado para el usuario: {}", email);
        }

        log.debug("Carrito obtenido: {}", cart);
        return cart;
    }

    /**
     * Agrega un producto al carrito de un usuario.
     * 
     * @param cartItem El producto a agregar al carrito.
     * @param email    El email del usuario.
     * @return El carrito actualizado del usuario.
     */
    @Override
    @PreAuthorize("#email == authentication.principal.username")
    public Cart addProductToCart(CartItem cartItem, String email) {
        log.debug("Agregando producto al carrito para el usuario: {}", email);

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.warn("Usuario no encontrado con email: {}", email);
            throw new UsernameNotFoundException("El usuario no existe.");
        }

        metricsService.sendFunnelEvent("add_to_cart", user.get().getId().toString(), Map.of(
                "product_id", cartItem.getProduct().getId().toString(),
                "quantity", cartItem.getQuantity()));

        Cart cart = cartRepository.findByUserId(user.get().getId());
        if (cart == null) {
            log.debug("Carrito no encontrado para el usuario. Creando nuevo carrito.");

            cart = Cart.builder()
                    .user(user.get())
                    .items(new ArrayList<>())
                    .totalPrice(0.0)
                    .build();

            cart = cartRepository.save(cart);
            log.info("Nuevo carrito creado para el usuario: {}", email);
        }

        Product product = productRepository.findById(cartItem.getProduct().getId());
        if (product == null || product.getPrice() == null) {
            log.error("Producto no encontrado o sin precio válido: {}", cartItem.getProduct().getId());
            throw new ResourceNotFoundException("El producto no existe o no tiene un precio válido.");
        }

        cartValidator.validateStockAvailability(product, cartItem.getQuantity());

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct() != null &&
                        item.getProduct().getId() != null &&
                        item.getProduct().getId().equals(cartItem.getProduct().getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            log.debug("Producto ya existe en el carrito. Actualizando cantidad de {} a {}",
                    item.getQuantity(), item.getQuantity() + cartItem.getQuantity());

            item.setQuantity(item.getQuantity() + cartItem.getQuantity());
        } else {
            cartItem.setProduct(product);
            log.debug("Añadiendo nuevo producto al carrito: {}", product.getId());
            cart.addItem(cartItem);
        }

        cart.recalculateTotalPrice();

        Cart updatedCart = cartRepository.save(cart);
        log.info("Carrito actualizado para el usuario: {}", email);

        return updatedCart;
    }

    @Override
    @PreAuthorize("#email == authentication.principal.username")
    public Cart updateProductInCart(CartItem cartItem, String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("El usuario no existe.");
        }

        metricsService.sendFunnelEvent("add_to_cart", user.get().getId().toString(), Map.of(
                "product_id", cartItem.getProduct().getId().toString(),
                "quantity", cartItem.getQuantity()));

        Product prod = productRepository.findById(cartItem.getProduct().getId());
        if (prod == null || prod.getPrice() == null) {
            log.error("Producto no encontrado o sin precio válido: {}", cartItem.getProduct().getId());
            throw new ResourceNotFoundException("El producto no existe o no tiene un precio válido.");
        }

        cartValidator.validateStockAvailability(prod, cartItem.getQuantity());

        Cart cart = cartRepository.findByUserId(user.get().getId());

        if (cart == null) {
            throw new ResourceNotFoundException("No existe carrito para el user seleccionado");
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct() != null &&
                        item.getProduct().getId() != null &&
                        item.getProduct().getId().equals(cartItem.getProduct().getId()))
                .findFirst();

        if (existingItem.isPresent()) {

            cart.updateQuantity(cartItem.getProduct().getId(), cartItem.getQuantity());
        }

        return cartRepository.save(cart);
    }

    /**
     * Elimina un producto del carrito.
     * 
     * @param productId El ID del producto a eliminar.
     * @param email     El email del usuario.
     */
    @Override
    @PreAuthorize("#email == authentication.principal.username")
    public void deleteProductFromCart(UUID productId, String email) {
        log.debug("Eliminando producto {} del carrito del usuario: {}", productId, email);

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.warn("Usuario no encontrado con email: {}", email);
            throw new UsernameNotFoundException("El usuario no existe.");
        }

        // Registrar evento para analíticas
        metricsService.sendFunnelEvent("remove_from_cart", user.get().getId().toString(), Map.of(
                "product_id", productId.toString()));

        // Obtener el carrito
        Cart cart = cartRepository.findByUserId(user.get().getId());
        if (cart == null || cart.getId() == null) {
            log.warn("Carrito no encontrado para el usuario: {}", email);
            return; // Si no hay carrito, no hay nada que eliminar
        }

        // Eliminar el producto del modelo de dominio
        cart.removeItem(productId);

        // Guardar el carrito actualizado
        cartRepository.save(cart);
        log.info("Carrito actualizado después de eliminar producto para el usuario: {}", email);
    }
}