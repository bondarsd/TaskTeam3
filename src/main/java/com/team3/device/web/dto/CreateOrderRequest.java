package com.team3.device.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "Customer ID must not be null")
    private Long customerId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequest> items;
}
