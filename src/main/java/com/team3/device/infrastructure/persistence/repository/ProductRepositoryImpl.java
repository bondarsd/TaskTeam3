package com.team3.device.infrastructure.persistence.repository;


import com.team3.device.domain.model.Product;
import com.team3.device.domain.repository.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;

    public ProductRepositoryImpl(JpaProductRepository jpaRepository) {
        this.jpaProductRepository = jpaRepository;
    }

    @Override
    public Product save(Product product) {
        return jpaProductRepository.save(product);
    }

    @Override
    public List<Product> findAll() {
        return jpaProductRepository.findAll();
    }

    @Override
    public boolean existsByName(String name) {
        return jpaProductRepository.existsByName(name);
    }
}
