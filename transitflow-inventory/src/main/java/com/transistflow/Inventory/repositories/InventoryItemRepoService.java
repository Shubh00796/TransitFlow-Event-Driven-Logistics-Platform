package com.transistflow.Inventory.repositories;


import com.transistflow.Inventory.domain.InventoryItemEntity;
import com.transistflow.commans.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryItemRepoService {

    private final InventoryItemRepository inventoryItemRepository;

    public InventoryItemEntity findByCompositeKey(Long warehouseId, Long productId) {
        return inventoryItemRepository.findByWarehouseIdAndProductId(warehouseId, productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Inventory item not found for warehouseId: " + warehouseId + " and productId: " + productId));
    }

    public Optional<Integer> getQuantityAvailable(Long warehouseId, Long productId) {
        return inventoryItemRepository.findQuantityAvailable(warehouseId, productId);
    }

    public boolean exists(Long warehouseId, Long productId) {
        return inventoryItemRepository.existsByWarehouseIdAndProductId(warehouseId, productId);
    }

    public InventoryItemEntity save(InventoryItemEntity entity) {
        return inventoryItemRepository.save(entity);
    }

    public List<InventoryItemEntity> saveAll(List<InventoryItemEntity> entities) {
        return inventoryItemRepository.saveAll(entities);
    }

    public void delete(InventoryItemEntity entity) {
        inventoryItemRepository.delete(entity);
    }

    public int updateQuantity(Long warehouseId, Long productId, Integer quantity) {
        return inventoryItemRepository.updateQuantity(warehouseId, productId, quantity);
    }
}
