package com.pesexpo.orderservice.client;

import com.pesexpo.orderservice.client.dto.ProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/api/v1/products")
public interface ProductClient {

    @GetExchange
    List<ProductResponse> findAllProducts();

    @GetExchange("/{uuid}")
    ProductResponse findProductByUuid(@PathVariable String uuid);



}
