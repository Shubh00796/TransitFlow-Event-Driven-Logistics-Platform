package com.transistflow.Inventory.domain;

import com.transistflow.Inventory.domain.InventoryItemId;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "inventory_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(InventoryItemId.class)
public class InventoryItemEntity {

    @Id
    private Long warehouseId;

    @Id
    private Long productId;

    private Integer quantityAvailable;

    private Instant lastChecked;

    /**
     * Checks if the inventory has at least the requested quantity.
     */
    public boolean hasSufficientStock(int requestedQuantity) {
        return this.quantityAvailable != null && this.quantityAvailable >= requestedQuantity;
    }

    /**
     * Updates (adds or subtracts) the inventory quantity.
     * This method handles locking/reservation logic.
     */
    public void updateQuantity(int delta) {
        if (this.quantityAvailable == null) {
            this.quantityAvailable = 0;
        }
        this.quantityAvailable += delta;
        this.lastChecked = Instant.now();
    }
}
