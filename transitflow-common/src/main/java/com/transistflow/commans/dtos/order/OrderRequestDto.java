package com.transistflow.commans.dtos.order;


import com.transistflow.commans.enmus.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    @NotNull(message = "customerId must not be null")
    private Long customerId;

    @NotBlank(message = "destination must not be blank")
    private String destination;

    @NotBlank(message = "origin must not be blank")
    private String origin;
    @NotNull(message = "status is required")
    private OrderStatus status;  // e.g. "CANCELLED"
    private List<OrderItemDto> items;
}

