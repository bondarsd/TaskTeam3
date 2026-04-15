package com.team3.device.web.controller;

import tools.jackson.databind.ObjectMapper;
import com.team3.device.application.service.CustomerApplicationService;
import com.team3.device.web.dto.CreateCustomerRequest;
import com.team3.device.web.dto.CustomerResponse;
import com.team3.device.web.exception.DuplicateEmailException;
import com.team3.device.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerApplicationService customerApplicationService;

    @Test
    void createCustomer_validRequest_returns200() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");

        CustomerResponse response = new CustomerResponse(1L, "John Doe", "john@example.com");
        when(customerApplicationService.createCustomer(any())).thenReturn(response);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void createCustomer_blankName_returns400() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("");
        request.setEmail("john@example.com");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCustomer_invalidNameChars_returns400() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("John123");
        request.setEmail("john@example.com");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCustomer_invalidEmail_returns400() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("John Doe");
        request.setEmail("not-an-email");

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCustomer_duplicateEmail_returns409() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");

        when(customerApplicationService.createCustomer(any()))
                .thenThrow(new DuplicateEmailException("Customer with email john@example.com already exists"));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void getCustomerById_exists_returns200() throws Exception {
        CustomerResponse response = new CustomerResponse(1L, "John Doe", "john@example.com");
        when(customerApplicationService.getCustomerById(eq(1L))).thenReturn(response);

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getCustomerById_notFound_returns404() throws Exception {
        when(customerApplicationService.getCustomerById(eq(99L)))
                .thenThrow(new ResourceNotFoundException("Customer with id 99 not found"));

        mockMvc.perform(get("/customers/99"))
                .andExpect(status().isNotFound());
    }
}