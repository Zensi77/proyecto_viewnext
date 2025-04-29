package com.juanma.proyecto_vn.interfaces.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juanma.proyecto_vn.Application.service.MetricsService;
import com.juanma.proyecto_vn.domain.service.IOrderService;
import com.juanma.proyecto_vn.interfaces.rest.dtos.order.CreateOrderDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.order.GetOrderDto;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private MetricsService metricsService;

    @Autowired
    private IOrderService orderService;

    @GetMapping
    public ResponseEntity<List<GetOrderDto>> getOrders(Authentication authentication) {

        String email = authentication.getName();
        List<GetOrderDto> orders = orderService.getAllOrders(email);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrderById(@RequestParam String id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String email = authentication.getName();
        GetOrderDto order = orderService.getOrderById(email, UUID.fromString(id));
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestBody @Valid CreateOrderDto orderDto,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String email = authentication.getName();
        long start = System.currentTimeMillis();
        GetOrderDto order = orderService.createOrder(orderDto, email);

        try {
            long end = System.currentTimeMillis();
            long processingTimeMs = end - start;

            metricsService.sendFunnelEvent("order_created", authentication.getName(),
                    Map.of("orderId", orderDto.getProductOrder().get(0).getProductId().toString(),
                            "processingTimeMs", processingTimeMs));
            return ResponseEntity.status(201).body(order);
        } catch (Exception e) {
            long processingTimeMs = System.currentTimeMillis() - start;

            metricsService.sendFunnelEvent("order_creation_failed", authentication.getName(),
                    Map.of("error", e.getMessage(), "processingTimeMs", processingTimeMs));
            return ResponseEntity.status(500).body("Error creating order: " + e.getMessage());
        }
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<Object> cancelOrder(@PathVariable String id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String email = authentication.getName();
        orderService.cancelOrder(UUID.fromString(id), email);
        return ResponseEntity.status(204).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOrder(@PathVariable String id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String email = authentication.getName();
        orderService.deleteOrder(UUID.fromString(id), email);
        return ResponseEntity.status(204).build();

    }
}
