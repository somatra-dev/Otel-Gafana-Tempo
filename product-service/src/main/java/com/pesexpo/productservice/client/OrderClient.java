package com.pesexpo.productservice.client;

import com.pesexpo.productservice.client.dto.OrderResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/api/v1/orders")
public interface OrderClient {

    @GetExchange
    List<OrderResponse> findAllOrders();

    @GetExchange("/{uuid}")
    OrderResponse findOrderByUuid(@PathVariable String uuid);

    @GetExchange("/product/{productUuid}")
    List<OrderResponse> findOrdersByProductUuid(@PathVariable String productUuid);

}
