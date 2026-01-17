package com.pesexpo.productservice.service.impl;

import com.pesexpo.productservice.client.OrderClient;
import com.pesexpo.productservice.client.dto.OrderResponse;
import com.pesexpo.productservice.client.dto.ProductOrdersResponse;
import com.pesexpo.productservice.domain.Product;
import com.pesexpo.productservice.domain.dto.CreateProduct;
import com.pesexpo.productservice.domain.dto.ResponseProduct;
import com.pesexpo.productservice.domain.dto.UpdateProduct;
import com.pesexpo.productservice.repository.ProductRepository;
import com.pesexpo.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final OrderClient orderClient;

    @Override
    @Transactional
    public void createProduct(CreateProduct createProduct) {
        Product product = new Product();
        product.setUuid(UUID.randomUUID().toString());
        product.setProductName(createProduct.productName());
        product.setPrice(createProduct.price());

        productRepository.save(product);
    }

    @Override
    public ResponseProduct findById(String uuid) {
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with uuid: " + uuid));

        return mapToResponseProduct(product);
    }

    @Override
    public List<ResponseProduct> findAll() {
        return productRepository.findAll().stream()
                .map(this::mapToResponseProduct)
                .toList();
    }

    @Override
    @Transactional
    public void updateProduct(String uuid, UpdateProduct updateProduct) {
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with uuid: " + uuid));

        if (updateProduct.productName() != null) {
            product.setProductName(updateProduct.productName());
        }
        if (updateProduct.price() != null) {
            product.setPrice(updateProduct.price());
        }

        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(String uuid) {
        if (!productRepository.existsByUuid(uuid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with uuid: " + uuid);
        }
        productRepository.deleteByUuid(uuid);
    }

    @Override
    public ProductOrdersResponse findProductWithOrders(String uuid) {
        ResponseProduct product = findById(uuid);

        // Fetch orders from order-service for this specific product
        List<OrderResponse> productOrders = orderClient.findOrdersByProductUuid(uuid);

        return ProductOrdersResponse.builder()
                .product(product)
                .orders(productOrders)
                .build();
    }

    private ResponseProduct mapToResponseProduct(Product product) {
        return ResponseProduct.builder()
                .uuid(product.getUuid())
                .productName(product.getProductName())
                .price(product.getPrice())
                .build();
    }

}
