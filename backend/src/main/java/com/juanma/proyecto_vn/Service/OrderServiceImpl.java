package com.juanma.proyecto_vn.Service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.juanma.proyecto_vn.Repositorys.UserRepository;
import com.juanma.proyecto_vn.models.Order;
import com.juanma.proyecto_vn.models.User;

public class OrderServiceImpl {
    @Autowired
    private MetricsService producerService;

    @Autowired
    private UserRepository userRepository;

    public void createOrder(Order order, String email) {
        Optional<User> user = userRepository.findByEmail(email).or(() -> {
            throw new UsernameNotFoundException("El usuario no existe.");
        });

        producerService.sendFunnelEvent("order_created", user.get().getId().toString(), Map.of(
                "order_id", order.getId(),
                "user_id", user.get().getId(),
                "order_total", order.getTotal_price(),
                "items_count", order.getProductOrder().size(),
                "average_price", order.getTotal_price() / order.getProductOrder().size(),
                "payment_method", order.getPaymentMethod()));
    }

    public void cancelOrder(Order order, String email) {
        Optional<User> user = userRepository.findByEmail(email).or(() -> {
            throw new UsernameNotFoundException("El usuario no existe.");
        });

        producerService.sendFunnelEvent("order_cancelled", user.get().getId().toString(), Map.of(
                "order_id", order.getId(),
                "user_id", user.get().getId(),
                "order_total", order.getTotal_price(),
                "items_count", order.getProductOrder().size(),
                "average_price", order.getTotal_price() / order.getProductOrder().size(),
                "payment_method", order.getPaymentMethod()));
    }
}
