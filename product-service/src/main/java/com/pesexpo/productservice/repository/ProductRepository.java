package com.pesexpo.productservice.repository;

import com.pesexpo.productservice.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findByUuid(String uuid);

    boolean existsByUuid(String uuid);

    void deleteByUuid(String uuid);

}
