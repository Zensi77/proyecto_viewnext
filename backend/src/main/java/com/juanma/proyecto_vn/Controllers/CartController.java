package com.juanma.proyecto_vn.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juanma.proyecto_vn.models.ProductCart;
import com.juanma.proyecto_vn.Service.CartServiceImpl;

@RestController("/api/v1/cart")
public class CartController {

    @Autowired
    private CartServiceImpl cartService;

    @GetMapping()
    public List<ProductCart> getCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        List<ProductCart> cart = cartService.getCartByUserId(userDetails.getUsername());

        return cart;
    }
}
