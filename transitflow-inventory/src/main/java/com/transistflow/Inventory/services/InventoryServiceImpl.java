package com.transistflow.Inventory.services;

import com.transistflow.Inventory.domain.InventoryItemEntity;
import com.transistflow.Inventory.mappers.InventoryItemMapper;
import com.transistflow.Inventory.repositories.InventoryItemRepoService;
import com.transistflow.commans.dtos.inventory.InventoryItemDto;
import com.transistflow.commans.events.InventoryUpdateFailedEvent;
import com.transistflow.commans.events.OrderCreatedEvent;
import com.transistflow.commans.events.OrderItemPayload;
import com.transistflow.commans.outbox.OutboxEventFactoryUtil;
import com.transistflow.commans.outbox.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryItemRepoService inventoryRepoService;
    private final InventoryItemMapper mapper;
    private final OutboxEventRepository outboxRepo;
    private final OutboxEventFactoryUtil outboxFactory;

    // ──────────────────────────────────────────────────────
    // Event-Driven Reservation Flow (with idempotency guard)
    // ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public void processOrderCreatedEvent(OrderCreatedEvent event) {
        Long orderId = event.getOrderId();
        Long warehouseId = event.getWarehouseId();

        // idempotency guard
        boolean duplicate = outboxRepo
                .existsByAggregateIdAndAggregateType(orderId.toString(), "ORDER_CREATED");
        if (duplicate) {
            log.info("⏭️ Skipping duplicate OrderCreatedEvent for orderId={}", orderId);
            return;
        }

        // record incoming event
        outboxRepo.save(
                outboxFactory.fromEvent(event, orderId.toString(), "ORDER_CREATED")
        );

        // reserve each line item
        for (OrderItemPayload item : event.getItems()) {
            reserveOrFail(orderId, warehouseId, item.getProductId(), item.getQuantity());
        }
    }

    private void reserveOrFail(
            Long orderId,
            Long warehouseId,
            Long productId,
            int qty
    ) {
        Optional<InventoryItemEntity> optEntity =
                Optional.ofNullable(inventoryRepoService.findByCompositeKey(warehouseId, productId));

        if (optEntity.isEmpty()) {
            emitFailure(orderId, productId, "Item not found");
            return;
        }

        InventoryItemEntity entity = optEntity.get();

        if (!entity.hasSufficientStock(qty)) {
            emitFailure(orderId, productId, "Insufficient stock");
            return;
        }

        // Deduct stock and persist
        entity.updateQuantity(-qty);
        inventoryRepoService.save(entity);

        log.info(
                "🔒 Reserved {} units of productId={} for orderId={} in warehouseId={}",
                qty, productId, orderId, warehouseId
        );
    }

    private void emitFailure(Long orderId, Long productId, String reason) {
        InventoryUpdateFailedEvent failed =
                InventoryUpdateFailedEvent.builder()
                        .orderId(orderId)
                        .productId(productId)
                        .reason(reason)
                        .timestamp(Instant.now())
                        .build();

        outboxRepo.save(
                outboxFactory.fromEvent(failed, orderId.toString(), "INVENTORY_UPDATE_FAILED")
        );

        log.warn(
                "📤 Inventory reservation failed: orderId={}, productId={}, reason={}",
                orderId, productId, reason
        );
    }

    // ──────────────────────────────────────────────────────
    // Standard CRUD and Utility Operations
    // ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public InventoryItemDto addInventoryItem(InventoryItemDto dto) {
        if (inventoryRepoService.exists(dto.getWarehouseId(), dto.getProductId())) {
            throw new IllegalStateException("Item already exists in inventory");
        }

        InventoryItemEntity entity = mapper.toEntity(dto);
        inventoryRepoService.save(entity);

        log.info(
                "✅ Inventory item added: warehouseId={}, productId={}",
                dto.getWarehouseId(), dto.getProductId()
        );
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public void updateQuantity(Long warehouseId, Long productId, int newQuantity) {
        int updated = inventoryRepoService.updateQuantity(
                warehouseId, productId, newQuantity
        );
        validateInventoryItem(updated);

        log.info(
                "✅ Updated inventory quantity: warehouseId={}, productId={}, newQty={}",
                warehouseId, productId, newQuantity
        );
    }

    @Override
    public boolean exists(Long warehouseId, Long productId) {
        return inventoryRepoService.exists(warehouseId, productId);
    }

    @Override
    public Optional<Integer> getAvailableQuantity(Long warehouseId, Long productId) {
        return inventoryRepoService.getQuantityAvailable(warehouseId, productId);
    }

    @Override
    public Optional<InventoryItemDto> getInventoryItem(Long warehouseId, Long productId) {
        InventoryItemEntity inventoryItem = inventoryRepoService
                .findByCompositeKey(warehouseId, productId);
        return Optional.ofNullable(mapper.toDto(inventoryItem));
    }

    @Override
    @Transactional
    public void adjustQuantity(Long warehouseId, Long productId, int delta) {
        int current = inventoryRepoService
                .getQuantityAvailable(warehouseId, productId)
                .orElseThrow(() -> new IllegalStateException(
                        "Inventory item not found: warehouseId=" + warehouseId
                                + ", productId=" + productId
                ));

        int newQty = current + delta;
        int rows = inventoryRepoService.updateQuantity(
                warehouseId, productId, newQty
        );

        if (rows == 0) {
            throw new IllegalArgumentException(
                    "Failed to adjust inventory (no rows affected) for warehouseId="
                            + warehouseId + ", productId=" + productId
            );
        }

        log.info(
                "🔄 adjustQuantity: warehouseId={}, productId={}, delta={}, newQty={}",
                warehouseId, productId, delta, newQty
        );
    }

    private static void validateInventoryItem(int rows) {
        if (rows == 0) {
            throw new IllegalArgumentException("Inventory item not found for update");
        }
    }
}
