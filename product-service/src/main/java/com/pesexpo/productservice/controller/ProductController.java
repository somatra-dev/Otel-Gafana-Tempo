package com.pesexpo.productservice.controller;

import com.pesexpo.productservice.client.dto.ProductOrdersResponse;
import com.pesexpo.productservice.domain.dto.CreateProduct;
import com.pesexpo.productservice.domain.dto.ResponseProduct;
import com.pesexpo.productservice.domain.dto.UpdateProduct;
import com.pesexpo.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<String> createProduct(@Valid @RequestBody CreateProduct createProduct) {
        productService.createProduct(createProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product created successfully");
    }

    @GetMapping
    public ResponseEntity<List<ResponseProduct>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping(path = "{uuid}")
    public ResponseEntity<ResponseProduct> findById(@PathVariable String uuid) {
        return ResponseEntity.ok(productService.findById(uuid));
    }

    @PutMapping(path = "{uuid}")
    public ResponseEntity<String> updateProduct(@PathVariable String uuid, @Valid @RequestBody UpdateProduct updateProduct) {
        productService.updateProduct(uuid, updateProduct);
        return ResponseEntity.ok("Product updated successfully");
    }

    @DeleteMapping(path = "{uuid}")
    public ResponseEntity<String> deleteProduct(@PathVariable String uuid) {
        productService.deleteProduct(uuid);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping(path = "{uuid}/orders")
    public ResponseEntity<ProductOrdersResponse> findProductWithOrders(@PathVariable String uuid) {
        return ResponseEntity.ok(productService.findProductWithOrders(uuid));
    }

}
