package com.juanma.proyecto_vn.interfaces.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juanma.proyecto_vn.domain.model.Order;
import com.juanma.proyecto_vn.domain.service.IOrderService;
import com.juanma.proyecto_vn.infrastructure.messaging.MetricsSender;
import com.juanma.proyecto_vn.interfaces.rest.dtos.order.CreateOrderDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.order.GetOrderDto;
import com.juanma.proyecto_vn.interfaces.rest.mapper.OrderDtoMapper;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador primario que implementa la API REST para pedidos
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final IOrderService orderService;
    private final OrderDtoMapper orderDtoMapper;
    private final MetricsSender metricsService;

    /**
     * Obtiene todos los pedidos del usuario autenticado
     */
    @GetMapping("/")
    public ResponseEntity<List<GetOrderDto>> getOrders(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        List<Order> orders = orderService.getAllOrders(email);

        return ResponseEntity.ok(orders.stream()
                .map(orderDtoMapper::toDto)
                .toList());
    }

    /**
     * Obtiene un pedido específico por su ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable UUID id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String email = authentication.getName();
        try {
            Order order = orderService.getOrderById(email, id);
            GetOrderDto orderDto = orderDtoMapper.toDto(order);
            return ResponseEntity.ok(orderDto);
        } catch (Exception e) {
            log.error("Error al obtener pedido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Crea un nuevo pedido
     */
    @PostMapping("/")
    public ResponseEntity<Object> createOrder(@Valid @RequestBody CreateOrderDto orderDto,
            BindingResult result,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        if (result.hasErrors()) {
            return getValidationErrorResponse(result);
        }

        String email = authentication.getName();

        try {
            // Convertir DTO a modelo de dominio
            Order orderRequest = orderDtoMapper.toDomain(orderDto);

            // Procesar la orden
            Order createdOrder = orderService.createOrder(orderRequest, email);

            // Convertir resultado a DTO
            GetOrderDto responseDto = orderDtoMapper.toDto(createdOrder);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            log.error("Error al crear pedido: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear pedido: " + e.getMessage());
        }
    }

    /**
     * Cancela un pedido existente
     */
    @PutMapping("/cancel/{id}")
    public ResponseEntity<Object> cancelOrder(@PathVariable UUID id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String email = authentication.getName();
        try {
            orderService.cancelOrder(id, email);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error al cancelar pedido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Elimina un pedido (sólo si está cancelado)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOrder(@PathVariable UUID id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String email = authentication.getName();
        try {
            orderService.deleteOrder(id, email);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error al eliminar pedido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Método auxiliar para crear respuesta de errores de validación
     */
    private ResponseEntity<Object> getValidationErrorResponse(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "Error en campo " + err.getField() + ": " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
