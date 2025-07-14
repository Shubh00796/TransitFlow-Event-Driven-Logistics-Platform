package com.transitflow.common.events;

import lombok.Data;

import java.time.Instant;

@Data
public class InventoryUpdatedEvent {
    private Long warehouseId;
    private Long productId;
    private Integer newQuantity;
    private Instant updatedAt;
}
