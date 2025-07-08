package com.transistflow.commans.dtos.order;


import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDto {
    private Long customerId;
    private String origin;
    private String destination;
    private List<OrderItemDto> items;
}

