package com.juanma.proyecto_vn.infrastructure.persistence.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.Cart;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.CartEntity;
import lombok.RequiredArgsConstructor;

/**
 * Mapper para convertir entre el modelo de dominio Cart y la entidad JPA
 * CartEntity.
 * Este mapper se encarga solo de la conversión básica entre Cart y CartEntity,
 * sin manejar los items del carrito directamente.
 */
@Component
@RequiredArgsConstructor
public class CartMapper {

    private final UserMapper userMapper;

    /**
     * Convierte una entidad Cart del dominio a una entidad JPA CartEntity
     * 
     * @param cart El modelo de dominio Cart
     * @return La entidad JPA CartEntity correspondiente
     */
    public CartEntity toEntity(Cart cart) {
        if (cart == null) {
            return null;
        }

        return CartEntity.builder()
                .id(cart.getId() != null ? UUID.fromString(cart.getId()) : null)
                .user(cart.getUser() != null ? userMapper.toEntity(cart.getUser()) : null)
                .total_price(cart.getTotalPrice())
                .build();
    }

    /**
     * Convierte una entidad JPA CartEntity a un modelo de dominio Cart
     * 
     * @param cartEntity La entidad JPA CartEntity
     * @return El modelo de dominio Cart correspondiente
     */
    public Cart toDomain(CartEntity cartEntity) {
        if (cartEntity == null) {
            return null;
        }

        return Cart.builder()
                .id(cartEntity.getId() != null ? cartEntity.getId().toString() : null)
                .user(cartEntity.getUser() != null ? userMapper.toDomain(cartEntity.getUser()) : null)
                .totalPrice(cartEntity.getTotal_price())
                .build();
    }
}
