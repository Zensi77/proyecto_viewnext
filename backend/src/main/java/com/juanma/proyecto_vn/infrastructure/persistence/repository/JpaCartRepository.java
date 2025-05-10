package com.juanma.proyecto_vn.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.CartEntity;

@Repository
public interface JpaCartRepository extends JpaRepository<CartEntity, UUID> {

    @Query("SELECT c FROM CartEntity c WHERE c.user.id = :userId")
    Optional<CartEntity> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);

}
