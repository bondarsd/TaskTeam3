package com.team3.device.web.controller;

import com.team3.device.application.service.OrderApplicationService;
import com.team3.device.web.dto.CreateOrderRequest;
import com.team3.device.web.dto.OrderItemRequest;
import com.team3.device.web.dto.OrderItemResponse;
import com.team3.device.web.dto.OrderResponse;
import com.team3.device.web.exception.BusinessValidationException;
import com.team3.device.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderApplicationService orderApplicationService;

    @Test
    void createOrder_validRequest_returns201() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(1L);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);

        request.setItems(List.of(item));

        OrderItemResponse itemResponse = new OrderItemResponse(
                1L, "Router X1", 2, BigDecimal.valueOf(50), BigDecimal.valueOf(100)
        );

        OrderResponse response = new OrderResponse(
                1L,
                1L,
                com.team3.device.domain.model.OrderStatus.CREATED,
                BigDecimal.valueOf(100),
                LocalDateTime.now(),
                List.of(itemResponse)
        );

        when(orderApplicationService.createOrder(any())).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customerId").value(1L))
                .andExpect(jsonPath("$.totalPrice").value(100));
    }

    @Test
    void createOrder_emptyItems_returns400() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(1L);
        request.setItems(List.of()); // invalid

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_customerNotFound_returns404() throws Exception {
        when(orderApplicationService.createOrder(any()))
                .thenThrow(new ResourceNotFoundException("Customer not found"));

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(99L);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(1);
        request.setItems(List.of(item));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrder_businessValidationError_returns400() throws Exception {
        when(orderApplicationService.createOrder(any()))
                .thenThrow(new BusinessValidationException("Invalid quantity"));

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(1L);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(0);
        request.setItems(List.of(item));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrder_validId_returns200() throws Exception {
        OrderResponse response = new OrderResponse(
                1L,
                1L,
                com.team3.device.domain.model.OrderStatus.CREATED,
                BigDecimal.valueOf(100),
                LocalDateTime.now(),
                List.of()
        );

        when(orderApplicationService.getOrderById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customerId").value(1L));
    }

    @Test
    void getOrder_notFound_returns404() throws Exception {
        when(orderApplicationService.getOrderById(99L))
                .thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound());
    }
}
