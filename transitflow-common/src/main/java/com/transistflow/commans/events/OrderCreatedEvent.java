package com.transistflow.commans.events;


import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class OrderCreatedEvent {
    private Long orderId;
    private Long customerId;
    private String origin;
    private String destination;
    private Instant createdAt;
    private List<OrderItemPayload> items;
}
