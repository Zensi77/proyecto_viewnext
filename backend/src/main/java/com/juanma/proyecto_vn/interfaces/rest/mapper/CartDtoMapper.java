package com.juanma.proyecto_vn.interfaces.rest.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.Cart;
import com.juanma.proyecto_vn.domain.model.CartItem;
import com.juanma.proyecto_vn.interfaces.rest.dtos.cart.CartDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.cart.GetProductCartDto;

import lombok.RequiredArgsConstructor;

/**
 * Mapeador para convertir entre modelos de dominio y DTOs para la API REST
 */
@Component
@RequiredArgsConstructor
public class CartDtoMapper {

    private final ProductDtoMapper productDtoMapper;

    /**
     * Convierte un modelo de dominio a un DTO para la API
     */
    public CartDto toDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        List<GetProductCartDto> productCartDtos = new ArrayList<>();
        if (cart.getItems() != null) {
            productCartDtos = cart.getItems().stream()
                    .map(this::mapCartItemToDto)
                    .collect(Collectors.toList());
        }

        return CartDto.builder()
                .cart_id(cart.getId())
                .products(productCartDtos)
                .build();
    }

    /**
     * Mapea un CartItem a un GetProductCartDto
     */
    private GetProductCartDto mapCartItemToDto(CartItem item) {
        return GetProductCartDto.builder()
                .product(productDtoMapper.toDto(item.getProduct()))
                .quantity(item.getQuantity())
                .build();
    }
}