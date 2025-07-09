package com.transistflow.commans.dtos.order;


import lombok.Data;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDto {
    private Long id;
    private Long customerId;
    private String origin;
    private String destination;
    private String status;
    private Instant createdAt;
    private Instant  updatedAt;
    private List<OrderItemDto> items;
}
