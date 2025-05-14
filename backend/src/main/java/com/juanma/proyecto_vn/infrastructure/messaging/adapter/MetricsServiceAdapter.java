package com.juanma.proyecto_vn.infrastructure.messaging.adapter;

import com.juanma.proyecto_vn.domain.service.IMetricsService;
import com.juanma.proyecto_vn.infrastructure.messaging.MetricsSender;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Adaptador que implementa el puerto de servicio de métricas
 * conectando con el sistema de mensajería existente
 */
@Component
@RequiredArgsConstructor
public class MetricsServiceAdapter implements IMetricsService {

    private final MetricsSender metricsSender;

    @Override
    public void sendFunnelEvent(String eventName, String userId, Map<String, Object> eventData) {
        metricsSender.sendFunnelEvent(eventName, userId, eventData);
    }

    @Override
    public void sendOrderMetrics(String eventName, String userId, Map<String, Object> eventData) {
        metricsSender.sendOrderMetrics(eventName, userId, eventData);
    }

    @Override
    public void sendMetrics(String eventName, String userId, Map<String, Object> eventData) {
        metricsSender.sendOrderMetrics(eventName, userId, eventData);
    }
}