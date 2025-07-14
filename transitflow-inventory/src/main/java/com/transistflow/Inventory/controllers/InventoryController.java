package com.transistflow.Inventory.controllers;

import com.transistflow.Inventory.services.InventoryService;
import com.transitflow.common.dtos.ApiResponse;
import com.transitflow.common.dtos.inventory.InventoryItemDto;
import com.transitflow.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Create a new inventory item.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<InventoryItemDto>> addItem(
            @RequestBody @Validated InventoryItemDto dto
    ) {
        InventoryItemDto created = inventoryService.addInventoryItem(dto);
        return ResponseEntity
                .ok(ApiResponse.success("Inventory item created", created));
    }

    /**
     * Overwrite the stock quantity of an existing item.
     */
    @PutMapping("/{warehouseId}/{productId}/quantity")
    public ResponseEntity<ApiResponse<Void>> updateQuantity(
            @PathVariable Long warehouseId,
            @PathVariable Long productId,
            @RequestParam int newQuantity
    ) {
        inventoryService.updateQuantity(warehouseId, productId, newQuantity);
        return ResponseEntity
                .ok(ApiResponse.success("Quantity updated", null));
    }

    /**
     * Adjust (add or subtract) stock by delta.
     */
    @PatchMapping("/{warehouseId}/{productId}/quantity")
    public ResponseEntity<ApiResponse<Void>> adjustQuantity(
            @PathVariable Long warehouseId,
            @PathVariable Long productId,
            @RequestParam int delta
    ) {
        inventoryService.adjustQuantity(warehouseId, productId, delta);
        return ResponseEntity
                .ok(ApiResponse.success("Quantity adjusted by " + delta, null));
    }

    /**
     * Retrieve the available quantity for a specific item.
     */
    @GetMapping("/{warehouseId}/{productId}/quantity")
    public ResponseEntity<ApiResponse<Integer>> getQuantity(
            @PathVariable Long warehouseId,
            @PathVariable Long productId
    ) {
        Integer qty = inventoryService.getAvailableQuantity(warehouseId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory item not found: warehouseId=" + warehouseId +
                                ", productId=" + productId
                ));
        return ResponseEntity
                .ok(ApiResponse.success("Quantity fetched", qty));
    }

    /**
     * Retrieve full details of a single inventory item.
     */
    @GetMapping("/{warehouseId}/{productId}")
    public ResponseEntity<ApiResponse<InventoryItemDto>> getItem(
            @PathVariable Long warehouseId,
            @PathVariable Long productId
    ) {
        InventoryItemDto dto = inventoryService.getInventoryItem(warehouseId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory item not found: warehouseId=" + warehouseId +
                                ", productId=" + productId
                ));
        return ResponseEntity
                .ok(ApiResponse.success("Inventory item fetched", dto));
    }


}
