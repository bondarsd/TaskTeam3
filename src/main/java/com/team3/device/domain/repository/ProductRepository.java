package com.team3.device.domain.repository;

import com.team3.device.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    List<Product> findAll();

    boolean existsByName(String name);

    Optional<Product> findById(Long id);
}
