package com.juanma.proyecto_vn.infrastructure.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import com.juanma.proyecto_vn.infrastructure.messaging.MetricsSender;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Order(2)
@Component
public class MetricsFilter extends OncePerRequestFilter {
    // implements Filter es otra forma de implementar el filtro, pero en este caso
    // se
    // utiliza OncePerRequestFilter

    @Autowired
    private MetricsSender metricsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long start = System.currentTimeMillis();
        String requestURI = request.getRequestURI();
        Object attr = request.getAttribute("userId");

        String userId = null;
        if (attr != null) {
            userId = attr.toString();
        }

        try {
            filterChain.doFilter(request, response); // Continuar con la cadena de filtros

            long time = System.currentTimeMillis() - start;

            Map<String, Object> extra = Map.of(
                    "url", requestURI,
                    "processing_time_ms", time);
            metricsService.sendMetrics("request_success", userId, request.getRequestURI(), extra);
        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;

            Map<String, Object> extra = Map.of(
                    "url", requestURI,
                    "processing_time_ms", time);

            metricsService.sendMetrics("request_error", userId, request.getRequestURI(), extra);
        }
    }
}
