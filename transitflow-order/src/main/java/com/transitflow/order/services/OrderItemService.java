package com.transitflow.order.services;

import com.transitflow.common.dtos.order.OrderItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Stream;

/**
 * Service port for OrderItem use‑cases.
 */
public interface OrderItemService {

    /**
     * Add a new item to an existing order.
     */
    OrderItemDto createOrderItem(Long orderId, OrderItemDto item);

    /**
     * Update an existing item on an order.
     */
    OrderItemDto updateOrderItem(Long orderId, Long itemId, OrderItemDto item);

    /**
     * Remove an item from an order.
     */
    void deleteOrderItem(Long orderId, Long itemId);

    /**
     * Page through items for a given order.
     */
    Page<OrderItemDto> getItemsByOrder(Long orderId, Pageable pageable);

    /**
     * Fetch all items for a given order (small lists).
     */
    List<OrderItemDto> getAllItemsByOrder(Long orderId);

    /**
     * Stream items for large‑volume processing.
     */
    Stream<OrderItemDto> streamItemsByOrder(Long orderId);
}
