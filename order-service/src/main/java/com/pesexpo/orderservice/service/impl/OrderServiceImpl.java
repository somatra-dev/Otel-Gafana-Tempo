package com.pesexpo.orderservice.service.impl;

import com.pesexpo.orderservice.client.ProductClient;
import com.pesexpo.orderservice.client.dto.ProductResponse;
import com.pesexpo.orderservice.domain.Order;
import com.pesexpo.orderservice.domain.OrderStatus;
import com.pesexpo.orderservice.domain.dto.CreateOrder;
import com.pesexpo.orderservice.domain.dto.ResponseOrder;
import com.pesexpo.orderservice.repository.OrderRepository;
import com.pesexpo.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    @Override
    public void createOrder(CreateOrder createOrder) {
        // Fetch product from product-service using HTTP Service Client
        ProductResponse product = productClient.findProductByUuid(createOrder.productUuid());

        Order order = new Order();
        order.setUuid(UUID.randomUUID().toString());
        order.setProductUuid(createOrder.productUuid());
        order.setQuantity(createOrder.quantity());
        order.setTotalPrice(product.price().multiply(java.math.BigDecimal.valueOf(createOrder.quantity())));
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        orderRepository.save(order);
    }

    @Override
    public List<ResponseOrder> findAll() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponseOrder)
                .toList();
    }

    @Override
    public ResponseOrder findByUuid(String uuid) {
        Order order = orderRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        return mapToResponseOrder(order);
    }

    @Override
    public List<ResponseOrder> findByProductUuid(String productUuid) {
        return orderRepository.findByProductUuid(productUuid).stream()
                .map(this::mapToResponseOrder)
                .toList();
    }

    @Override
    @Transactional
    public void deleteOrder(String uuid) {
        if (!orderRepository.existsByUuid(uuid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        orderRepository.deleteByUuid(uuid);
    }

    private ResponseOrder mapToResponseOrder(Order order) {
        // Fetch product details from product-service
        ProductResponse product = productClient.findProductByUuid(order.getProductUuid());

        return ResponseOrder.builder()
                .uuid(order.getUuid())
                .product(product)
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .build();
    }

}
