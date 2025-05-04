package com.juanma.proyecto_vn.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.OrderEntity;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findAllByUserId(UUID userId);
}
