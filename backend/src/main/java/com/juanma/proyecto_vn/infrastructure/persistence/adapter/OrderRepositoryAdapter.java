package com.juanma.proyecto_vn.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductOrderEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.JpaProductRepository;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;
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
    private final JpaProductRepository jpaProductRepository;
    private final OrderMapper orderMapper;

    @Override
    public Order save(Order order) {
        Order orderToSave = Order.builder()
                .userId(order.getUserId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .build();

        OrderEntity orderSaved = jpaOrderRepository.save(orderMapper.toEntity(orderToSave));

        List<ProductOrderEntity> productOrderEntities = order.getItems().stream()
                .map(item -> {
                    ProductOrderEntity.ProductOrderPK pk = new ProductOrderEntity.ProductOrderPK();
                    pk.setOrderId(orderSaved.getId());
                    pk.setProductId(item.getProduct().getId());

                    return ProductOrderEntity.builder()
                            .id(pk)
                            .product(jpaProductRepository.findById(item.getProduct().getId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Product not found")))
                            .quantity(item.getQuantity())
                            .order(orderSaved)
                            .build();
                })
                .collect(Collectors.toList());

        orderSaved.setProductOrder(productOrderEntities);

        jpaOrderRepository.save(orderSaved);

        return orderMapper.toDomain(orderSaved);

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
    public Order cancelOrder(UUID id) {
        OrderEntity orderEntity = jpaOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El pedido no existe."));
        orderEntity.setStatus("CANCELLED");
        jpaOrderRepository.save(orderEntity);

        // Restaurar stock
        for (ProductOrderEntity productOrder : orderEntity.getProductOrder()) {
            jpaProductRepository.findById(productOrder.getProduct().getId())
                    .ifPresent(product -> {
                        product.setStock(product.getStock() + productOrder.getQuantity());
                        jpaProductRepository.save(product);
                    });
        }

        return orderMapper.toDomain(orderEntity);
    }

    @Override
    public void delete(Order order) {
        OrderEntity orderEntity = orderMapper.toEntity(order);
        jpaOrderRepository.delete(orderEntity);
    }
}