package com.juanma.proyecto_vn.domain.service;

import java.util.Map;

/**
 * Puerto de salida (secundario) para el envío de métricas
 */
public interface IMetricsService {
    void sendFunnelEvent(String eventName, String userId, Map<String, Object> eventData);

    void sendOrderMetrics(String eventName, String userId, Map<String, Object> eventData);
    
    void sendMetrics(String eventName, String userId, Map<String, Object> eventData);
}