package com.juanma.proyecto_vn.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import com.juanma.proyecto_vn.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.OrderEntity;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {
    OrderEntity save(OrderEntity order);
    List<OrderEntity> findAllByUserId(UUID userId);
}
