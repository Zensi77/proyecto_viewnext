package com.juanma.proyecto_vn.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class MetricsSender {

    private static final Logger log = LoggerFactory.getLogger(MetricsSender.class);
    @Autowired
    private JmsTemplate jms;
    @Value("ips/ips.json")
    private String ipsDirectory;

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
        ArrayList<Double> location = getLocation();

        Map<String, Object> metric = new HashMap<>();
        metric.put("id", "FUNNEL_EVENT");
        metric.put("event", event);
        metric.put("timestamp", Instant.now().toString());
        metric.put("user_id", userId);
        metric.put("lat", location.get(0));
        metric.put("lng", location.get(1));
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


    private ArrayList<Double> getLocation() {
        String clientIp = MDC.get("clientIp");
        ArrayList<Double> res = new ArrayList();

        if (clientIp.equals("0:0:0:0:0:0:0:1") || clientIp.equals("127.0.0.1")) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                InputStream input = new ClassPathResource(this.ipsDirectory).getInputStream();

                ArrayList<Map<String, Object>> ips = mapper.readValue(input, ArrayList.class);

                int randomIndex = (int) (Math.random() * ips.size());

                Map<String, Object> location = ips.get(randomIndex);

                res.add((Double) location.get("lat"));
                res.add((Double) location.get("lng"));
            } catch (IOException e) {
                log.error("Error al cargar el archivo de IPs: {}", ipsDirectory);
            }
        }

        return res;
    }
}
