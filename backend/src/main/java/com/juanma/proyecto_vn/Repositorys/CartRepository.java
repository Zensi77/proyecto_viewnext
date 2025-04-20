package com.juanma.proyecto_vn.Repositorys;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juanma.proyecto_vn.models.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
}
