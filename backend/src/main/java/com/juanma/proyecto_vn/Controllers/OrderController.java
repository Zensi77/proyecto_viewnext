package com.juanma.proyecto_vn.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juanma.proyecto_vn.Service.MetricsService;
import com.juanma.proyecto_vn.models.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private MetricsService producerService;

    @PostMapping("")
    public ResponseEntity<Object> createOrder(@RequestBody String entity) {
        long start = System.currentTimeMillis();

        try {
            // Aquí iría la lógica para crear la orden
            // ...

            long end = System.currentTimeMillis();
            long processingTimeMs = end - start;

            // Enviar métricas a Kafka
            // producerService.sendOrderMetrics(new Order(), processingTimeMs, "success",
            // null);
            producerService.sendFunnelEvent("order_created", null, null);
            return ResponseEntity.ok("Order created successfully");
        } catch (Exception e) {
            long end = System.currentTimeMillis();
            long processingTimeMs = end - start;

            // producerService.sendOrderMetrics(null, processingTimeMs, "Failure",
            // e.getMessage());

            return ResponseEntity.status(500).body("Error creating order: " + e.getMessage());
        }
    }

}
