package com.juanma.proyecto_vn.Controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juanma.proyecto_vn.models.ProductCart;
import com.juanma.proyecto_vn.Service.CartServiceImpl;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartServiceImpl cartService;

    @GetMapping()
    public ResponseEntity<?> getCart(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = authentication.getName();
        List<ProductCart> cart = cartService.getCartByUserId(email);

        return ResponseEntity.ok(cart);
    }
}
