package com.transistflow.order.controllers;

import com.transistflow.commans.dtos.ApiResponse;
import com.transistflow.commans.dtos.order.OrderItemDto;
import com.transistflow.order.services.OrderItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/{orderId}/items")
@RequiredArgsConstructor
@Slf4j
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderItemDto>> createItem(
            @PathVariable Long orderId,
            @RequestBody OrderItemDto itemDto
    ) {
        log.info("Request to create order item for orderId={}", orderId);
        OrderItemDto created = orderItemService.createOrderItem(orderId, itemDto);
        return ResponseEntity.ok(ApiResponse.success("Order item created successfully", created));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ApiResponse<OrderItemDto>> updateItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestBody OrderItemDto itemDto
    ) {
        log.info("Request to update order item itemId={} for orderId={}", itemId, orderId);
        OrderItemDto updated = orderItemService.updateOrderItem(orderId, itemId, itemDto);
        return ResponseEntity.ok(ApiResponse.success("Order item updated successfully", updated));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId
    ) {
        log.info("Request to delete order item itemId={} for orderId={}", itemId, orderId);
        orderItemService.deleteOrderItem(orderId, itemId);
        return ResponseEntity.ok(ApiResponse.success("Order item deleted successfully", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderItemDto>>> getAllItems(
            @PathVariable Long orderId
    ) {
        log.info("Request to fetch all items for orderId={}", orderId);
        List<OrderItemDto> items = orderItemService.getAllItemsByOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("Fetched all order items", items));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<Page<OrderItemDto>>> getPagedItems(
            @PathVariable Long orderId,
            Pageable pageable
    ) {
        log.info("Request to fetch paged items for orderId={}", orderId);
        Page<OrderItemDto> pagedItems = orderItemService.getItemsByOrder(orderId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Fetched paginated order items", pagedItems));
    }
}
