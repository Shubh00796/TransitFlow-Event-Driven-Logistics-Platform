package com.transistflow.commans.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class OrderStatusChangedEvent {
    private Long orderId;
    private String oldStatus;
    private String newStatus;
    private Instant occurredAt;
}
