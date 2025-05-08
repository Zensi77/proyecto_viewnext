package com.juanma.proyecto_vn.domain.repository;

import java.util.UUID;

import com.juanma.proyecto_vn.domain.model.Cart;

public interface CartRepository {
    Cart save(Cart cart);

    Cart findByUserId(UUID id);

    void deleteByUserId(UUID id);
}
