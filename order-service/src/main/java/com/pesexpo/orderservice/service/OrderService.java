package com.pesexpo.orderservice.service;

import com.pesexpo.orderservice.domain.dto.CreateOrder;
import com.pesexpo.orderservice.domain.dto.ResponseOrder;

import java.util.List;

public interface OrderService {

    void createOrder(CreateOrder createOrder);

    List<ResponseOrder> findAll();

    ResponseOrder findByUuid(String uuid);

    List<ResponseOrder> findByProductUuid(String productUuid);

    void deleteOrder(String uuid);

}
