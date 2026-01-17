package com.pesexpo.orderservice.client.dto;

import java.math.BigDecimal;

public record ProductResponse(
        String uuid,
        String productName,
        BigDecimal price
) {
}
