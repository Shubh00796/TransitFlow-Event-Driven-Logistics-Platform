package com.transistflow.Inventory.domain;


import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * Composite key for inventory item: (warehouseId + productId)
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InventoryItemId implements Serializable {

    private Long warehouseId;
    private Long productId;
}
