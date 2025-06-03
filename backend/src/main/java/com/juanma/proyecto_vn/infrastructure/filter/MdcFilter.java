package com.juanma.proyecto_vn.infrastructure.filter;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Filter para añadir un identificador único a cada petición
 * y guardarlo en el MDC (Mapped Diagnostic Context)
 * EL MDC es un mapa que se utiliza para almacenar información
 * diagnóstica que se puede asociar a un hilo específico
 * y que se puede utilizar en los logs de forma automática.
 * Esto permite rastrear las peticiones y sus respuestas
 */
@Component
public class MdcFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain) throws ServletException, IOException {
        String clientIp = req.getRemoteAddr();
        String requestId = UUID.randomUUID().toString();

        MDC.put("requestId", requestId);
        MDC.put("clientIp", clientIp);
        try {
            chain.doFilter(req, res);
        } finally {
            MDC.clear();
        }
    }
}
