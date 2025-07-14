package com.transitflow.common.dtos.inventory;


import lombok.Data;
import java.time.Instant;

@Data
public class InventoryItemDto {
    private Long warehouseId;
    private Long productId;
    private Integer quantityAvailable;
    private Instant lastChecked;
}
