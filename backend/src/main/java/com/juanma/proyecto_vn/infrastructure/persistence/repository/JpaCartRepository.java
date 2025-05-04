package com.juanma.proyecto_vn.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.CartEntity;

@Repository
public interface JpaCartRepository extends JpaRepository<CartEntity, UUID> {
    Optional<CartEntity> findByUserId(UUID userId);
}
