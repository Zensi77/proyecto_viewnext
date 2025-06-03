package com.juanma.proyecto_vn.Application.usecase.order;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.juanma.proyecto_vn.domain.model.*;
import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juanma.proyecto_vn.Application.validator.OrderValidator;
import com.juanma.proyecto_vn.domain.repository.CartRepository;
import com.juanma.proyecto_vn.domain.repository.OrderRepository;
import com.juanma.proyecto_vn.domain.repository.ProductRepository;
import com.juanma.proyecto_vn.domain.repository.UserRepository;
import com.juanma.proyecto_vn.domain.service.IOrderService;
import com.juanma.proyecto_vn.infrastructure.messaging.MetricsSender;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.IllegalOrderStateException;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de los casos de uso relacionados con pedidos
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements IOrderService {

    @Value("${stripe.api.key}")
    private String apiKey;

    @PostConstruct()
    public void init() {
        Stripe.apiKey = apiKey;
    }

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final MetricsSender metricsService;
    private final OrderValidator orderValidator;

    @Override
    @PreAuthorize("#email == authentication.principal.username")
    public List<Order> getAllOrders(String email) {
        User user = getUserByEmail(email);
        return orderRepository.findAllByUserId(user.getId());
    }

    @Override
    @PreAuthorize("#email == authentication.principal.username")
    public Order getOrderById(String email, UUID orderId) {
        User user = getUserByEmail(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("El pedido no existe."));

        if (!order.getUserId().equals(user.getId())) {
            throw new ResourceNotFoundException("El pedido no pertenece al usuario.");
        }

        return order;
    }

    @Override
    @PreAuthorize("#email == authentication.principal.username")
    public Order createOrder(Order orderRequest, String email) {
        User user = getUserByEmail(email);
        log.debug("Creando pedido para usuario: {}", orderRequest.getId());

        try {
            // Limpiar la caché del validador antes de iniciar la validación
            orderValidator.clearCache();

            // Establecer valores iniciales
            orderRequest.setUserId(user.getId());
            orderRequest.calculateTotalPrice();

            // Validar la orden completa
            List<String> validationErrors = orderValidator.validateOrder(orderRequest);
            if (!validationErrors.isEmpty()) {
                String errorMessage = String.join("; ", validationErrors);
                throw new IllegalArgumentException("Error en la validación de la orden: " + errorMessage);
            }

            // Verificar y completar la información de productos
            for (OrderItem item : orderRequest.getItems()) {
                orderValidator.checkStockAvailability(item);

                Product product = productRepository.findById(item.getProduct().getId());

                product.setStock(product.getStock() - item.getQuantity());
                productRepository.save(product);
            }

            // Eliminar el carrito del usuario
            Cart cart = cartRepository.findByUserId(user.getId());
            cart.cleanCart();
            cartRepository.save(cart);

            // Guardar el pedido para asignarle un id
            Order savedOrder = orderRepository.save(orderRequest);

            log.info("Pedido creado con éxito: ID={}, Usuario={}, Total={}, Items={}",
                    savedOrder.getId(), user.getId(), savedOrder.getTotalPrice(), savedOrder.getItems().size());

            // Enviar métricas
            metricsService.sendFunnelEvent("order_created", user.getId().toString(), Map.of(
                    "order_total", savedOrder.getTotalPrice(),
                    "items_count", savedOrder.getItems().size(),
                    "payment_method", savedOrder.getPaymentMethod().toString()));

            return savedOrder;
        } catch (Exception e) {
            log.error("Error al crear pedido para usuario {}: {}", email, e.getMessage(), e);
            throw new IllegalArgumentException("Error al crear el pedido: " + e.getMessage());
        }
    }

    @Override
    @PreAuthorize("#email == authentication.principal.username")
    public void cancelOrder(UUID orderId, String email) {
        // Actualizar el estado
        Order order = orderRepository.cancelOrder(orderId);

        log.info("Pedido cancelado: ID={}", order.getId());

        // Enviar métricas
        metricsService.sendFunnelEvent("order_cancelled", order.getUserId().toString(), Map.of(
                "order_total", order.getTotalPrice(),
                "items_count", order.getItems().size(),
                "payment_method", order.getPaymentMethod().toString()));
    }

    @Override
    @PreAuthorize("#email == authentication.principal.username")
    public void deleteOrder(UUID orderId, String email) {
        User user = getUserByEmail(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("El pedido no existe."));

        if (!order.getUserId().equals(user.getId())) {
            throw new ResourceNotFoundException("El pedido no pertenece al usuario.");
        }

        // Sólo permitir eliminar pedidos cancelados
        if (!"CANCELLED".equals(order.getStatus())) {
            throw new IllegalOrderStateException("Sólo se pueden eliminar pedidos cancelados.");
        }

        orderRepository.delete(order);
        log.info("Pedido eliminado: ID={}, Usuario={}", order.getId(), user.getId());
    }

    /**
     * Método auxiliar para obtener el usuario por email
     */
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario no existe."));
    }
}