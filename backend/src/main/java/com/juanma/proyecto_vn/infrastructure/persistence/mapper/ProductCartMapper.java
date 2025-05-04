package com.juanma.proyecto_vn.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.CartItem;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductCartEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductCartMapper {
    private final ProductMapper productMapper;
    private final CartMapper cartMapper;

    public ProductCartEntity toEntity(CartItem productCart) {
        if (productCart == null) {
            return null;
        }

        return ProductCartEntity.builder()
                .cart(cartMapper.toEntity(productCart.getCart()))
                .product(productMapper.toEntity(productCart.getProduct()))
                .quantity(productCart.getQuantity())
                .build();
    }

    public CartItem toDomain(ProductCartEntity productCartEntity) {
        if (productCartEntity == null) {
            return null;
        }

        return CartItem.builder()
                .product(productMapper.toDomain(productCartEntity.getProduct()))
                .cart(cartMapper.toDomain(productCartEntity.getCart()))
                .quantity(productCartEntity.getQuantity())
                .build();
    }
}
