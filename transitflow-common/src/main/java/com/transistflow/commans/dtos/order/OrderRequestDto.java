package com.transistflow.commans.dtos.order;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    private Long customerId;
    @NotBlank(message = "destination must not be blank")
    private String destination;

    @NotNull(message = "status is required")
    private String status;  // e.g. "CANCELLED"
    private List<OrderItemDto> items;
}

