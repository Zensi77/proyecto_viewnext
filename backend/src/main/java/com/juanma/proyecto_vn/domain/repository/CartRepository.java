package com.juanma.proyecto_vn.domain.repository;

import com.juanma.proyecto_vn.domain.model.Cart;

public interface CartRepository {
    Cart save(Cart cart);

    Cart findByUserId(String id);

}
