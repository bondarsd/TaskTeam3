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

    /**
     * Creates a new order for a customer.
     * Loads customer and products, checks stock availability,
     * calculates totals, updates inventory, and persists the order atomically.
     *
     * @param request order creation payload
     * @return created order as response DTO
     * @throws BusinessValidationException if stock is insufficient
     * @throws ResourceNotFoundException if order, customer or product does not exist
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

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

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id: " + id));

        return mapToOrderResponse(order);
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

    private OrderItem createOrderItem(OrderItemRequest request) {
        Product product = loadProduct(request.getProductId());
        validateStock(product, request.getQuantity());

        BigDecimal unitPrice = product.getPrice();
        BigDecimal lineTotal = calculateLineTotal(unitPrice, request.getQuantity());

        updateStock(product, request.getQuantity());

        return buildOrderItem(product, request.getQuantity(), unitPrice, lineTotal);
    }

    private void validateStock(Product product, int quantity) {
        if (product.getStock() < quantity) {
            throw new BusinessValidationException(
                    "Insufficient stock for product id: " + product.getId());
        }
    }

    private BigDecimal calculateLineTotal(BigDecimal unitPrice, int quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private void updateStock(Product product, int quantity) {
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    private OrderItem buildOrderItem(Product product, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setLineTotal(lineTotal);
        return item;
    }

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
