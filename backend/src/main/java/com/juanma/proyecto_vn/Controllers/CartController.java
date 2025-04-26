package com.juanma.proyecto_vn.Controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

import com.juanma.proyecto_vn.Dtos.Cart.CartDto;
import com.juanma.proyecto_vn.Dtos.Cart.CreateProductCartDto;
import com.juanma.proyecto_vn.Repositorys.UserRepository;
import com.juanma.proyecto_vn.Service.CartServiceImpl;
import com.juanma.proyecto_vn.models.Cart;
import com.juanma.proyecto_vn.models.User;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping()
    public ResponseEntity<?> getCart(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = authentication.getName();
        CartDto cart = cartService.getCartByUserId(email);

        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProductCart(Authentication authentication,
            @RequestBody @Valid CreateProductCartDto productCartDto, BindingResult result) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        } else if (result.hasErrors()) {
            return validation(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addProductToCart(productCartDto, authentication.getName()));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteProductFromCart(
            @RequestParam(required = true) UUID product_id,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<User> user = userRepository.findByEmail(authentication.getName());

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("El usuario no existe.");
        }

        CartDto cart = cartService.getCartByUserId(user.get().getId().toString());

        cartService.deleteProductFromCart(product_id, UUID.fromString(cart.getCart_id()), authentication.getName());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put("message", "Error en campo " + err.getField() + ": " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

}
