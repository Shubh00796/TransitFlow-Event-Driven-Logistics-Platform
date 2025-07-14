package com.transitflow.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdateFailedEvent {
    private Long orderId;
    private Long productId;
    private String reason;
    private Instant timestamp;
}