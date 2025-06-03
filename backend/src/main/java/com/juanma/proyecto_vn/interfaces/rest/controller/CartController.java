package com.juanma.proyecto_vn.interfaces.rest.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

import com.juanma.proyecto_vn.domain.model.Cart;
import com.juanma.proyecto_vn.domain.model.CartItem;
import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.domain.service.ICartService;
import com.juanma.proyecto_vn.interfaces.rest.dtos.cart.CartDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.cart.CreateProductCartDto;
import com.juanma.proyecto_vn.interfaces.rest.mapper.CartDtoMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final ICartService cartService;
    private final CartDtoMapper cartDtoMapper;

    @GetMapping("/")
    public ResponseEntity<?> getCart(Authentication authentication) {
        log.info("Solicitud para obtener carrito del usuario: {}",
                authentication != null ? authentication.getName() : "anónimo");

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }

        String email = authentication.getName();
        Cart cart = cartService.getCartByUserId(email);
        log.debug("Carrito obtenido: {}", cart);
        CartDto cartDto = cartDtoMapper.toDto(cart, String.valueOf(cart.getUser().getId()));

        log.info("Carrito obtenido para usuario: {}", email);
        return ResponseEntity.ok(cartDto);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProductCart(
            Authentication authentication,
            @RequestBody @Valid CreateProductCartDto productCartDto,
            BindingResult result) {

        log.info("Solicitud para añadir producto al carrito: {}", productCartDto);


        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }

        if (result.hasErrors()) {
            return validation(result);
        }

        // Convertir DTO a objeto de dominio CartItem
        CartItem cartItem = convertToCartItem(productCartDto);
        Cart updatedCart = cartService.addProductToCart(cartItem, authentication.getName());
        CartDto responseDto = cartDtoMapper.toDto(updatedCart, String.valueOf(updatedCart.getUser().getId()));

        log.info("Producto añadido al carrito del usuario: {}", authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteProductFromCart(
            @RequestParam(required = true) UUID product_id,
            Authentication authentication) {

        log.info("Solicitud para eliminar producto {} del carrito", product_id);

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }

        cartService.deleteProductFromCart(product_id, authentication.getName());

        log.info("Producto {} eliminado del carrito del usuario: {}", product_id, authentication.getName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/")
    public ResponseEntity<?> updateProductCart(
            @RequestBody @Valid CreateProductCartDto productCartDto,
            BindingResult result,
            Authentication authentication) {

        log.info("Solicitud para actualizar producto en el carrito: {}", productCartDto);

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }

        if (result.hasErrors()) {
            return validation(result);
        }

        // Convertir DTO a objeto de dominio CartItem
        CartItem cartItem = convertToCartItem(productCartDto);
        Cart updatedCart = cartService.updateProductInCart(cartItem, authentication.getName());
        CartDto responseDto = cartDtoMapper.toDto(updatedCart, String.valueOf(updatedCart.getUser().getId()));

        log.info("Producto actualizado en el carrito del usuario: {}", authentication.getName());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Convierte un DTO de producto para carrito a un CartItem del dominio
     * 
     * @param dto DTO con la información del producto a añadir al carrito
     * @return CartItem con la información necesaria para el servicio
     */
    private CartItem convertToCartItem(CreateProductCartDto dto) {
        return CartItem.builder()
                .product(Product.builder()
                        .id(dto.getProduct_id())
                        .build())
                .quantity(dto.getQuantity())
                .build();
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}
