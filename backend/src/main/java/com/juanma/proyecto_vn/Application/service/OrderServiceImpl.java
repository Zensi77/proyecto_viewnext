package com.juanma.proyecto_vn.Application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.juanma.proyecto_vn.domain.service.IOrderService;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.Order;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.Product;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductOrder;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.User;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.OrderRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.ProductRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.UserRepository;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;
import com.juanma.proyecto_vn.interfaces.rest.dtos.category.CategoryDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.order.CreateOrderDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.order.GetOrderDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.order.GetProductOrderDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.product.GetProductDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.provider.ProviderDto;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private MetricsService producerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @PreAuthorize("#email == authentication.principal.username")
    @Transactional
    public List<GetOrderDto> getAllOrders(String email) {
        Optional<User> user = userRepository.findByEmail(email).or(() -> {
            throw new UsernameNotFoundException("El usuario no existe.");
        });

        List<Order> orders = orderRepository.findAllByUserId(user.get().getId());

        return orders.stream()
                .map(this::convertToGetOrderDto)
                .toList();
    }

    @Override
    @PreAuthorize("#email == authentication.principal.username")
    @Transactional
    public GetOrderDto getOrderById(String email, UUID orderId) {
        Optional<User> user = userRepository.findByEmail(email).or(() -> {
            throw new UsernameNotFoundException("El usuario no existe.");
        });

        Optional<Order> order = orderRepository.findById(orderId).or(() -> {
            throw new ResourceNotFoundException("El pedido no existe.");
        });

        if (!order.get().getUser().getId().equals(user.get().getId())) {
            throw new UsernameNotFoundException("El pedido no pertenece al usuario.");
        }

        return convertToGetOrderDto(order.get());

    }

    @Override
    @PreAuthorize("#email == authentication.principal.username")
    @Transactional
    public GetOrderDto createOrder(CreateOrderDto orderDto, String email) {
        Optional<User> user = userRepository.findByEmail(email).or(() -> {
            throw new UsernameNotFoundException("El usuario no existe.");
        });

        Order orderEntity = Order.builder()
                .user(user.get())
                .productOrder(new ArrayList<>())
                .total_price(orderDto.getTotal_price())
                .status("PENDING")
                .paymentMethod(orderDto.getPaymentMethod())
                .build();

        Order order = orderRepository.save(orderEntity);

        List<ProductOrder> productOrders = orderDto.getProductOrder().stream().map(
                productOrderDto -> {
                    Optional<Product> optProduct = productRepository.findById(productOrderDto.getProductId()).or(() -> {
                        throw new ResourceNotFoundException("El producto no existe.");
                    });

                    Product product = optProduct.get();

                    ProductOrder.ProductOrderPK productOrderPK = new ProductOrder.ProductOrderPK();
                    productOrderPK.setProductId(product.getId());
                    productOrderPK.setOrderId(order.getId());

                    ProductOrder productOrder = ProductOrder.builder()
                            .id(productOrderPK)
                            .product(product)
                            .order(order)
                            .quantity(productOrderDto.getQuantity())
                            .build();

                    return productOrder;
                }).collect(Collectors.toList());

        order.setProductOrder(productOrders);
        orderRepository.save(order); // Guarda la orden con los productos

        producerService.sendFunnelEvent("order_created", user.get().getId().toString(), Map.of(
                "order_id", order.getId(),
                "order_total", order.getTotal_price(),
                "items_count", order.getProductOrder().size(),
                "average_price", order.getTotal_price() / order.getProductOrder().size(),
                "payment_method", order.getPaymentMethod()));

        return convertToGetOrderDto(order);
    }

    @Override
    @PreAuthorize("#email == authentication.principal.username")
    @Transactional
    public void cancelOrder(UUID orderId, String email) {
        Optional<User> user = userRepository.findByEmail(email).or(() -> {
            throw new UsernameNotFoundException("El usuario no existe.");
        });

        Optional<Order> order = orderRepository.findById(orderId).or(() -> {
            throw new ResourceNotFoundException("El pedido no existe.");
        });

        if (!order.get().getUser().getId().equals(user.get().getId())) {
            throw new UsernameNotFoundException("El pedido no pertenece al usuario.");
        }

        order.get().setStatus("CANCELLED");

        orderRepository.save(order.get());

        producerService.sendFunnelEvent("order_cancelled", user.get().getId().toString(), Map.of(
                "order_id", order.get().getId(),
                "user_id", user.get().getId(),
                "order_total", order.get().getTotal_price(),
                "items_count", order.get().getProductOrder().size(),
                "average_price", order.get().getTotal_price() / order.get().getProductOrder().size(),
                "payment_method", order.get().getPaymentMethod()));
    }

    @Override
    @PreAuthorize("#email == authentication.principal.username")
    public void deleteOrder(UUID orderId, String email) {
        Optional<User> user = userRepository.findByEmail(email).or(() -> {
            throw new UsernameNotFoundException("El usuario no existe.");
        });

        Optional<Order> order = orderRepository.findById(orderId).or(() -> {
            throw new ResourceNotFoundException("El pedido no existe.");
        });

        if (!order.get().getUser().getId().equals(user.get().getId())) {
            throw new UsernameNotFoundException("El pedido no pertenece al usuario.");
        }

        orderRepository.delete(order.get());
    }

    private GetOrderDto convertToGetOrderDto(Order order) {
        return GetOrderDto.builder()
                .id(order.getId().toString())
                .productOrder(convertToGetProductCartDto(order.getProductOrder()))
                .total_price(order.getTotal_price())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private List<GetProductOrderDto> convertToGetProductCartDto(List<ProductOrder> order) {
        return order.stream()
                .map(product -> GetProductOrderDto.builder()
                        .product(GetProductDto.builder()
                                .id(product.getProduct().getId())
                                .name(product.getProduct().getName())
                                .price(product.getProduct().getPrice())
                                .image(product.getProduct().getImage())
                                .description(product.getProduct().getDescription())
                                .stock(product.getProduct().getStock())
                                .provider(ProviderDto.builder()
                                        .id(product.getProduct().getProvider().getId())
                                        .name(product.getProduct().getProvider().getName())
                                        .address(product.getProduct().getProvider().getAddress())
                                        .build())
                                .category(CategoryDto.builder()
                                        .id(product.getProduct().getCategory().getId())
                                        .name(product.getProduct().getCategory().getName())
                                        .build())
                                .build())
                        .quantity(product.getQuantity())
                        .build())
                .toList();
    }

}
