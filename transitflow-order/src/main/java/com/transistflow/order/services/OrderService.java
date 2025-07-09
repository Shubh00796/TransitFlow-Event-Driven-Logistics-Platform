package com.transistflow.order.services;


import com.transistflow.commans.dtos.order.OrderRequestDto;
import com.transistflow.commans.dtos.order.OrderResponseDto;
import com.transistflow.commans.dtos.order.OrderUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

/**
 * Service port for Order use‑cases.
 */
public interface OrderService {

    /**
     * Create a new order (with items).
     */
    OrderResponseDto createOrder(OrderRequestDto request);

    /**
     * Update mutable fields of an existing order (destination, status).
     */
    OrderResponseDto updateOrder(Long orderId, OrderUpdateRequestDto request);

    /**
     * Delete an order by its ID.
     */
    void deleteOrder(Long orderId);

    /**
     * Fetch a single order by ID.
     */
    OrderResponseDto getOrderById(Long orderId);

    /**
     * Page through orders for a specific customer.
     */
    Page<OrderResponseDto> getOrdersByCustomer(Long customerId, Pageable pageable);

    /**
     * Slice through orders in the given status.
     */
    Slice<OrderResponseDto> getOrdersByStatus(String status, Pageable pageable);

    /**
     * Quickly fetch the top 100 most recently updated orders in a status.
     */
    List<OrderResponseDto> getTop100ByStatus(String status);

    /**
     * Stream orders created before the cutoff (for batch or export jobs).
     */
    Stream<OrderResponseDto> streamOrdersCreatedBefore(Instant cutoff);
}
