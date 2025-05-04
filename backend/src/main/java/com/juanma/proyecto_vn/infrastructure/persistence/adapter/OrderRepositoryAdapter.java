package com.juanma.proyecto_vn.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.Order;
import com.juanma.proyecto_vn.domain.repository.OrderRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.OrderEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.mapper.OrderMapper;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.JpaOrderRepository;

import lombok.RequiredArgsConstructor;

/**
 * Adaptador que implementa el puerto de repositorio de pedidos conectando con
 * JPA
 */
@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;
    private final OrderMapper orderMapper;

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = orderMapper.toEntity(order);
        OrderEntity savedEntity = jpaOrderRepository.save(orderEntity);
        return orderMapper.toDomain(savedEntity);
    }

    @Override
    public List<Order> findAllByUserId(UUID userId) {
        List<OrderEntity> orderEntities = jpaOrderRepository.findAllByUserId(userId);
        return orderEntities.stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Order> findById(UUID id) {
        Optional<OrderEntity> orderEntityOpt = jpaOrderRepository.findById(id);
        return orderEntityOpt.map(orderMapper::toDomain);
    }

    @Override
    public void delete(Order order) {
        OrderEntity orderEntity = orderMapper.toEntity(order);
        jpaOrderRepository.delete(orderEntity);
    }
}