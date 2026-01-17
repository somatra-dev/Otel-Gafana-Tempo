package com.pesexpo.productservice.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateProduct(

        @NotBlank(message = "Product Name can't be null")
        String productName,

        @NotNull(message = "Product Price can't be null")
        @Positive(message = "Product Price must be positive")
        BigDecimal price

) {
}
