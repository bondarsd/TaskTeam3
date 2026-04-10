package com.team3.device.web.controller;

import com.team3.device.application.service.CustomerApplicationService;
import com.team3.device.web.dto.CreateCustomerRequest;
import com.team3.device.web.dto.CustomerResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerApplicationService customerApplicationService;

    public CustomerController(CustomerApplicationService customerApplicationService) {
        this.customerApplicationService = customerApplicationService;
    }

    @PostMapping
    public CustomerResponse createCustomer(@RequestBody CreateCustomerRequest request) {
        return customerApplicationService.createCustomer(request);
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomerById(@PathVariable Long id) {
        return customerApplicationService.getCustomerById(id);
    }
}
