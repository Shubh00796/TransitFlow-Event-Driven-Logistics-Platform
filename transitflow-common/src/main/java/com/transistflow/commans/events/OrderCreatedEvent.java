package com.transistflow.commans.events;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderCreatedEvent {
    private Long orderId;
    private Long customerId;
    private String origin;
    private String destination;
    private Instant createdAt;
    private Long warehouseId;
    private List<OrderItemPayload> items;
}
