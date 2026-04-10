package com.team3.device.application.service;

import com.team3.device.domain.model.Customer;
import com.team3.device.infrastructure.persistence.repository.JpaCustomerRepository;
import com.team3.device.web.dto.CreateCustomerRequest;
import com.team3.device.web.dto.CustomerResponse;
import com.team3.device.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerApplicationService {

    private final JpaCustomerRepository repository;

    public CustomerApplicationService(JpaCustomerRepository repository) {
        this.repository = repository;
    }

    public CustomerResponse createCustomer(CreateCustomerRequest request) {

        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();

        Customer savedCustomer = repository.save(customer);

        return new CustomerResponse(
                savedCustomer.getId(),
                savedCustomer.getName(),
                savedCustomer.getEmail()
        );
    }

    public CustomerResponse getCustomerById(Long id) {

        Customer customer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail()
        );
    }
}
