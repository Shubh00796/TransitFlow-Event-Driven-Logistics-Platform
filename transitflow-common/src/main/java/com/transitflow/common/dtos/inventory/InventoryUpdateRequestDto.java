package com.transitflow.common.dtos.inventory;


import lombok.Data;

@Data
public class InventoryUpdateRequestDto {
    private Long warehouseId;
    private Long productId;
    private Integer delta; // how much to increase/decrease
}
