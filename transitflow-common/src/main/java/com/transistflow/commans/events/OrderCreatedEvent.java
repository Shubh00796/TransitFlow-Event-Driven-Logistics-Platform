package com.transistflow.commans.events;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {
    private Long orderId;
    private Long customerId;
    private String origin;
    private String destination;
    private Instant createdAt;
    private Long warehouseId;
    private List<OrderItemPayload> items;
}
