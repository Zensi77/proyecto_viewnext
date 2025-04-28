package com.juanma.proyecto_vn.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    @Autowired
    private JmsTemplate jms;

    private static final Logger log = LoggerFactory.getLogger(MetricsService.class);

    public void sendOrderMetrics(String event, String userId, Map<String, Object> extra) {
        Map<String, Object> metric = new HashMap<>();
        metric.put("event", event);
        metric.put("timestamp", Instant.now().toString());
        metric.put("user_id", userId);

        metric.putAll(extra);
        // metric.put("order_total", order.getTotal_price());
        // metric.put("items_count", order.getProductOrder().size());
        // metric.put("average_price", order.getTotal_price() /
        // order.getProductOrder().size());
        // metric.put("payment_method", order.getPaymentMethod());
        // metric.put("status", status);
        // metric.put("order_id", order.getId());
        // metric.put("error_message", errorMessage);
        // metric.put("processing_time", processingTimeMs);

        jms.convertAndSend("metrics-queue", metric);

        log.info("Enviando métrica de orden: {}", metric);
    }

    /**
     * Envía un evento de funnel a la cola de métricas.
     * 
     * conversion_rate = orders_created / checkout_started × 100%
     * cart_abandonment_rate = (add_to_cart – checkout_started) / add_to_cart × 100%
     * cart_completion_rate = checkout_started / add_to_cart × 100%
     * 
     * @param event     El nombre del evento.
     * @param userId    El ID del usuario.
     * @param sessionId El ID de la sesión.
     * @param extra     Información adicional sobre el evento.
     */
    public void sendFunnelEvent(String event, String userId, Map<String, Object> extra) {
        Map<String, Object> metric = new HashMap<>();
        metric.put("event", event);
        metric.put("timestamp", Instant.now().toString());
        metric.put("user_id", userId);
        metric.putAll(extra);
        jms.convertAndSend("metrics-queue", metric);

        log.info("Enviando métrica de funnel: {}", metric);
    }

    public void sendMetrics(String event, String userId, String url, Map<String, Object> extra) {
        Map<String, Object> metric = new HashMap<>();
        metric.put("event", event);
        metric.put("timestamp", Instant.now().toString());
        metric.put("user_id", userId);
        metric.putAll(extra);

        jms.convertAndSend("metrics-queue", metric);

        log.info("Enviando métrica de rendimiento: {}", metric);
    }

}
