package com.transistflow.Inventory.services;


import com.transitflow.common.dtos.inventory.InventoryItemDto;
import com.transitflow.common.events.OrderCreatedEvent;

import java.util.Optional;

public interface InventoryService {
    /**
     * process or conusme the event that has been created
     */
    void processOrderCreatedEvent(OrderCreatedEvent event);


    /**
     * Create or add a new inventory item.
     */
    InventoryItemDto addInventoryItem(InventoryItemDto dto);

    /**
     * Update inventory quantity for a specific product in a warehouse.
     * Fails if the item doesn't exist.
     */
    void updateQuantity(Long warehouseId, Long productId, int newQuantity);

    /**
     * Check if an inventory item exists.
     */
    boolean exists(Long warehouseId, Long productId);

    /**
     * Get quantity available for a product in a warehouse.
     */
    Optional<Integer> getAvailableQuantity(Long warehouseId, Long productId);

    /**
     * Get full details of inventory item.
     */
    Optional<InventoryItemDto> getInventoryItem(Long warehouseId, Long productId);


    /**
     * Adjust quantity based on order event or stock return.
     * (delta can be negative or positive)
     */
    void adjustQuantity(Long warehouseId, Long productId, int delta);
}
