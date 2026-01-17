package com.pesexpo.productservice.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        String uuid,
        String productUuid,
        Integer quantity,
        BigDecimal totalPrice,
        LocalDateTime orderDate,
        String status
) { }
