package com.juanma.proyecto_vn.infrastructure.persistence.adapter;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.CartItem;
import com.juanma.proyecto_vn.domain.repository.ProductCartRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.mapper.ProductCartMapper;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.JpaProductCartRepository;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductCartAdapter implements ProductCartRepository {
    private final JpaProductCartRepository jpaProductCartRepository;
    private final ProductCartMapper productCartMapper;

    @Override
    public CartItem save(CartItem cartItem) {
        return productCartMapper.toDomain(jpaProductCartRepository.save(productCartMapper.toEntity(cartItem)));
    }

    @Override
    public CartItem findByProductAndCart(UUID productId, UUID cartId) {
        return jpaProductCartRepository.findByProductAndCart(productId, cartId)
                .map(productCartMapper::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart"));
    }

    @Override
    public void deleteByProductAndCart(UUID productId, UUID cartId) {
        jpaProductCartRepository.deleteByProductAndCart(productId, cartId);
    }

}
