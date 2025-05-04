package com.juanma.proyecto_vn.infrastructure.persistence.mapper;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.Cart;
import com.juanma.proyecto_vn.domain.model.CartItem;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.CartEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductCartEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartMapper {
    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    public CartEntity toEntity(Cart cart) {
        if (cart == null) {
            return null;
        }

        return CartEntity.builder()
                .id(UUID.fromString(cart.getId()))
                .user(userMapper.toEntity(cart.getUser()))
                .total_price(cart.getTotalPrice())
                .build();
    }

    public Cart toDomain(CartEntity entity) {
        if (entity == null) {
            return null;
        }

        return Cart.builder()
                .id(entity.getId().toString())
                .user(userMapper.toDomain(entity.getUser()))
                .totalPrice(entity.getTotal_price())
                .build();
    }

    public List<CartItem> mapProductOrderToDomain(List<ProductCartEntity> productOrder) {
        return productOrder.stream()
                .map(item -> CartItem.builder()
                        .product(productMapper.toDomain(item.getProduct()))
                        .quantity(item.getQuantity())
                        .build())
                .toList();
    }

    public List<ProductCartEntity> mapProductOrderToEntity(List<CartItem> productOrder) {
        return productOrder.stream()
                .map(item -> ProductCartEntity.builder()
                        .product(productMapper.toEntity(item.getProduct()))
                        .quantity(item.getQuantity())
                        .build())
                .toList();
    }
}
