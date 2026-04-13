package com.team3.device.infrastructure.persistence.repository;

import com.team3.device.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCustomerId(Long customerId);

}