package com.team3.device.domain.repository;

import com.team3.device.domain.model.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository {

    Product save(Product product);

    List<Product> findAll();

}
