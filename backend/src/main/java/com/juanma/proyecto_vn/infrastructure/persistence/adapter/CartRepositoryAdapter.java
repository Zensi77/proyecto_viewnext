package com.juanma.proyecto_vn.infrastructure.persistence.adapter;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.Cart;
import com.juanma.proyecto_vn.domain.repository.CartRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.CartEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.mapper.CartMapper;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.JpaCartRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartRepositoryAdapter implements CartRepository {

    private final JpaCartRepository jpaCartRepository;
    private final CartMapper cartMapper;

    @Override
    public Cart save(Cart cart) {
        CartEntity cartEntity = jpaCartRepository.save(cartMapper.toEntity(cart));

        return cartMapper.toDomain(cartEntity);
    }

    @Override
    public Cart findByUserId(String id) {
        CartEntity cartEntity = jpaCartRepository.findByUserId(UUID.fromString(id))
                .orElse(null);
        return cartMapper.toDomain(cartEntity);
    }

}
