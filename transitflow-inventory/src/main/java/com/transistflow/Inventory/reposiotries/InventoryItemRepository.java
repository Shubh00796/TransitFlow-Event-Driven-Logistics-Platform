package com.transistflow.Inventory.reposiotries;


import com.transistflow.Inventory.domain.InventoryItemEntity;
import com.transistflow.Inventory.domain.InventoryItemId;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItemEntity, InventoryItemId> {

    Optional<InventoryItemEntity> findByWarehouseIdAndProductId(Long warehouseId, Long productId);

    boolean existsByWarehouseIdAndProductId(Long warehouseId, Long productId);

    @Query("SELECT i.quantityAvailable FROM InventoryItemEntity i WHERE i.warehouseId = :warehouseId AND i.productId = :productId")
    Optional<Integer> findQuantityAvailable(Long warehouseId, Long productId);

    // ✅ Bulk quantity update
    @Modifying
    @Query("""
                UPDATE InventoryItemEntity i
                SET i.quantityAvailable = :quantity
                WHERE i.warehouseId = :warehouseId AND i.productId = :productId
            """)
    int updateQuantity(@Param("warehouseId") Long warehouseId,
                       @Param("productId") Long productId,
                       @Param("quantity") Integer quantity);

}
