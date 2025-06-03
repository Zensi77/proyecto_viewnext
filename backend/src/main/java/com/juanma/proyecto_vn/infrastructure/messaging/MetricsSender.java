package com.juanma.proyecto_vn.infrastructure.messaging;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.jms.JmsException;

@Service
public class MetricsSender {

    @Autowired
    private JmsTemplate jms;

    private static final Logger log = LoggerFactory.getLogger(MetricsSender.class);

    public void sendOrderMetrics(String event, String userId, Map<String, Object> extra) {
        Map<String, Object> metric = new HashMap<>();
        metric.put("id", "ORDER_EVENT");
        metric.put("event", event);
        metric.put("timestamp", Instant.now().toString());
        metric.put("user_id", userId);

        metric.putAll(extra);

        try {
            jms.convertAndSend("metrics-queue", metric);
        } catch (JmsException e) {
            log.error("Error al enviar la métrica de orden: {}", e.getMessage());
        }

        log.info("Enviando métrica de orden: {}", metric);
    }

    /**
     * Envía un evento de funnel a la cola de métricas.
     * conversion_rate = order_created / checkout_started × 100%
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
        metric.put("id", "FUNNEL_EVENT");
        metric.put("event", event);
        metric.put("timestamp", Instant.now().toString());
        metric.put("user_id", userId);
        metric.putAll(extra);

        try {
            jms.convertAndSend("metrics-queue", metric);
        } catch (JmsException e) {
            log.error("Error al enviar la métrica de funnel: {}", e.getMessage());
        }

        log.info("Enviando métrica de funnel: {}", metric);
    }

    public void sendMetrics(String event, String userId, String url, Map<String, Object> extra) {
        Map<String, Object> metric = new HashMap<>();
        metric.put("id", "PERFORMANCE_EVENT");
        metric.put("event", event);
        metric.put("url", url);
        metric.put("timestamp", Instant.now().toString());
        metric.put("user_id", userId);
        // todo: meter en extra el status code si hay un error
        metric.putAll(extra);

        try {
            jms.convertAndSend("metrics-queue", metric);
        } catch (JmsException e) {
            log.error("Error al enviar la métrica de rendimiento: {}", e.getMessage());
        }

        log.info("Enviando métrica de rendimiento: {}", metric);
    }

}
