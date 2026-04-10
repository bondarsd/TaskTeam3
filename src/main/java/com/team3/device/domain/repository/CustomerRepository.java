package com.team3.device.domain.repository;

import com.team3.device.domain.model.Customer;

import java.util.Optional;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(Long id);
}
