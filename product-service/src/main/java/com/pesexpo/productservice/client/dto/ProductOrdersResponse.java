package com.pesexpo.productservice.client.dto;

import com.pesexpo.productservice.domain.dto.ResponseProduct;
import lombok.Builder;

import java.util.List;

@Builder
public record ProductOrdersResponse(
        ResponseProduct product,
        List<OrderResponse> orders
) {
}
