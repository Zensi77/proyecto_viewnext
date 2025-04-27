package com.juanma.proyecto_vn.Infrastructure.Filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.juanma.proyecto_vn.Service.MetricsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MetricsFilter extends OncePerRequestFilter {
    // implements Filter es otra forma de implementar el filtro, pero en este caso
    // se
    // utiliza OncePerRequestFilter

    @Autowired
    private MetricsService metricsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response); // Continuar con la cadena de filtros
        } catch (Exception e) {
            // Handle exception if needed
            metricsService.sendMetrics("request_error", 1, request.getRequestURI(), true);
        } finally {
            long end = System.currentTimeMillis();
            long processingTimeMs = end - start;
            metricsService.sendMetrics("request_processing_time", processingTimeMs, request.getRequestURI(), false);
        }
    }
}
