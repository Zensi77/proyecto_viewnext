package com.juanma.proyecto_vn.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.Cart;
import com.juanma.proyecto_vn.domain.model.CartItem;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.CartEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductCartEntity;

import lombok.RequiredArgsConstructor;

/**
 * Mapper para convertir entre el modelo de dominio CartItem y la entidad JPA
 * ProductCartEntity.
 */
@Component
@RequiredArgsConstructor
public class ProductCartMapper {
    private final ProductMapper productMapper;

    /**
     * Convierte un CartItem del dominio a un ProductCartEntity
     * 
     * @param cartItem   El modelo de dominio CartItem
     * @param cartEntity La entidad CartEntity asociada
     * @return La entidad JPA ProductCartEntity correspondiente
     */
    public ProductCartEntity toEntity(CartItem cartItem, CartEntity cartEntity) {
        if (cartItem == null || cartEntity == null) {
            return null;
        }

        return ProductCartEntity.builder()
                .id(cartItem.getId())
                .cart(cartEntity)
                .product(productMapper.toEntity(cartItem.getProduct()))
                .quantity(cartItem.getQuantity())
                .build();
    }

    /**
     * Convierte un ProductCartEntity a un CartItem del dominio
     * 
     * @param productCartEntity La entidad JPA ProductCartEntity
     * @param cart              El modelo de dominio Cart asociado
     * @return El modelo de dominio CartItem correspondiente
     */
    public CartItem toDomain(ProductCartEntity productCartEntity, Cart cart) {
        if (productCartEntity == null) {
            return null;
        }

        return CartItem.builder()
                .id(productCartEntity.getId())
                .product(productMapper.toDomain(productCartEntity.getProduct()))
                .cart(cart)
                .quantity(productCartEntity.getQuantity())
                .build();
    }
}
