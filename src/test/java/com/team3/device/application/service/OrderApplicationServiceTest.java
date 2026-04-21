package com.team3.device.application.service;

import com.team3.device.domain.model.Customer;
import com.team3.device.domain.model.Order;
import com.team3.device.domain.model.OrderStatus;
import com.team3.device.domain.model.Product;
import com.team3.device.domain.repository.CustomerRepository;
import com.team3.device.domain.repository.OrderRepository;
import com.team3.device.domain.repository.ProductRepository;
import com.team3.device.web.dto.CreateOrderRequest;
import com.team3.device.web.dto.OrderItemRequest;
import com.team3.device.web.exception.BusinessValidationException;
import com.team3.device.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderApplicationServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderApplicationService service;

    private Customer customer;
    private Product product;

    @BeforeEach
    void setup() {
        customer = new Customer();
        customer.setId(1L);

        product = new Product();
        product.setId(1L);
        product.setName("Router X1");
        product.setPrice(BigDecimal.valueOf(50));
        product.setStock(10);
    }

    @Test
    void createOrder_success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // Build request
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(1L);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);

        request.setItems(List.of(item));

        var response = service.createOrder(request);

        assertEquals(1L, response.getCustomerId());
        assertEquals(OrderStatus.CREATED, response.getStatus());
        assertEquals(BigDecimal.valueOf(100), response.getTotalPrice());
        assertEquals(8, product.getStock()); // stock reduced

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void createOrder_invalidCustomer_throwsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(1L);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(1);

        request.setItems(List.of(item));

        assertThrows(ResourceNotFoundException.class, () -> service.createOrder(request));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_invalidProduct_throwsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(1L);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(1);

        request.setItems(List.of(item));

        assertThrows(ResourceNotFoundException.class, () -> service.createOrder(request));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_insufficientStock_throwsException() {
        product.setStock(1);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(1L);

        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(5);

        request.setItems(List.of(item));

        assertThrows(BusinessValidationException.class, () -> service.createOrder(request));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_totalCalculationCorrect() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(1L);

        OrderItemRequest item1 = new OrderItemRequest();
        item1.setProductId(1L);
        item1.setQuantity(1);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setProductId(1L);
        item2.setQuantity(3);

        request.setItems(List.of(item1, item2));

        var response = service.createOrder(request);

        assertEquals(BigDecimal.valueOf(200), response.getTotalPrice()); // 1*50 + 3*50
    }

    @Test
    void getOrderById_success() {
        Order order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(BigDecimal.valueOf(100));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        var response = service.getOrderById(1L);

        assertEquals(1L, response.getId());
        assertEquals(1L, response.getCustomerId());
        assertEquals(OrderStatus.CREATED, response.getStatus());
        assertEquals(BigDecimal.valueOf(100), response.getTotalPrice());

        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_notFound_throwsException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getOrderById(99L));

        verify(orderRepository).findById(99L);
    }
}
