package com.team3.device.infrastructure.persistence.repository;

import com.team3.device.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCustomerRepository extends JpaRepository<Customer, Long> {
}