package com.pesexpo.productservice.domain.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ResponseProduct(

        String uuid,

        String productName,

        BigDecimal price

) { }
