package com.pesexpo.orderservice.repository;

import com.pesexpo.orderservice.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.stereotype.Repository;import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Optional<Order> findByUuid(String uuid);

    List<Order> findByProductUuid(String productUuid);

    void deleteByUuid(String uuid);

    boolean existsByUuid(String uuid);

}
