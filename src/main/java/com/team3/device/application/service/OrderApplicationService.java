package com.team3.device.application.service;

import com.team3.device.domain.model.Customer;
import com.team3.device.domain.model.Order;
import com.team3.device.domain.model.OrderItem;
import com.team3.device.domain.model.OrderStatus;
import com.team3.device.domain.model.Product;
import com.team3.device.domain.repository.CustomerRepository;
import com.team3.device.domain.repository.OrderRepository;
import com.team3.device.domain.repository.ProductRepository;
import com.team3.device.web.dto.CreateOrderRequest;
import com.team3.device.web.dto.OrderItemRequest;
import com.team3.device.web.dto.OrderItemResponse;
import com.team3.device.web.dto.OrderResponse;
import com.team3.device.web.exception.BusinessValidationException;
import com.team3.device.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderApplicationService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderApplicationService(CustomerRepository customerRepository,
                                   ProductRepository productRepository,
                                   OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    // Create Order
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        validateItemsNotEmpty(request);

        Customer customer = loadCustomer(request.getCustomerId());

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.CREATED);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItem orderItem = createOrderItem(itemRequest);
            order.addItem(orderItem);
            total = total.add(orderItem.getLineTotal());
        }

        order.setTotalPrice(total);

        Order saved = orderRepository.save(order);
        return mapToOrderResponse(saved);
    }

    // Get Order
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id: " + id));

        return mapToOrderResponse(order);
    }

    // Helpers - Validation
    private void validateItemsNotEmpty(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessValidationException("Order must contain at least one item");
        }
    }

    private Customer loadCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + customerId));
    }

    private Product loadProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + productId));
    }

    // Helpers - Order Item Creation
    private OrderItem createOrderItem(OrderItemRequest request) {

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BusinessValidationException("Quantity must be greater than 0");
        }

        Product product = loadProduct(request.getProductId());

        if (product.getStock() < request.getQuantity()) {
            throw new BusinessValidationException(
                    "Insufficient stock for product id: " + product.getId());
        }

        BigDecimal unitPrice = product.getPrice();
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

        // decrease stock
        product.setStock(product.getStock() - request.getQuantity());
        productRepository.save(product);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(unitPrice);
        item.setLineTotal(lineTotal);

        return item;
    }

    // Helpers - Mapping
    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomer().getId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                items
        );
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getLineTotal()
        );
    }
}
