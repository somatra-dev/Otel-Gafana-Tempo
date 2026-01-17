package com.pesexpo.productservice.service;

import com.pesexpo.productservice.client.dto.ProductOrdersResponse;
import com.pesexpo.productservice.domain.dto.CreateProduct;
import com.pesexpo.productservice.domain.dto.ResponseProduct;
import com.pesexpo.productservice.domain.dto.UpdateProduct;

import java.util.List;

public interface ProductService {

    void createProduct(CreateProduct createProduct);

    ResponseProduct findById(String uuid);

    List<ResponseProduct> findAll();

    void updateProduct(String uuid, UpdateProduct updateProduct);

    void deleteProduct(String uuid);

    ProductOrdersResponse findProductWithOrders(String uuid);

}
