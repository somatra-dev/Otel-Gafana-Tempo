package com.pesexpo.orderservice.controller;

import com.pesexpo.orderservice.domain.dto.CreateOrder;
import com.pesexpo.orderservice.domain.dto.ResponseOrder;
import com.pesexpo.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> createOrder(@Valid @RequestBody CreateOrder createOrder) {
        orderService.createOrder(createOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body("Order created successfully");
    }

    @GetMapping
    public ResponseEntity<List<ResponseOrder>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping(path = "{uuid}")
    public ResponseEntity<ResponseOrder> findByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(orderService.findByUuid(uuid));
    }

    @GetMapping(path = "product/{productUuid}")
    public ResponseEntity<List<ResponseOrder>> findByProductUuid(@PathVariable String productUuid) {
        return ResponseEntity.ok(orderService.findByProductUuid(productUuid));
    }

    @DeleteMapping(path = "{uuid}")
    public ResponseEntity<String> deleteOrder(@PathVariable String uuid) {
        orderService.deleteOrder(uuid);
        return ResponseEntity.ok("Order deleted successfully");
    }

}
