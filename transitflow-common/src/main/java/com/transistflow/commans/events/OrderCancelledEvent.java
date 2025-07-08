package com.transistflow.commans.events;


import lombok.Data;

import java.time.Instant;

@Data
public class OrderCancelledEvent {
    private Long orderId;
    private String reason;
    private Instant cancelledAt;
}
