package com.pesexpo.orderservice.domain.dto;

import com.pesexpo.orderservice.client.dto.ProductResponse;
import com.pesexpo.orderservice.domain.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ResponseOrder(
        String uuid,
        ProductResponse product,
        Integer quantity,
        BigDecimal totalPrice,
        LocalDateTime orderDate,
        OrderStatus status
) {
}
