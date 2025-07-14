package com.transitflow.order.reposiotries;


import com.transitflow.common.enmus.OrderStatus;
import com.transitflow.order.domain.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    /**
     * Fetch orders for a specific customer, paged.
     * Uses idx_orders_customer_id.
     */
    Page<OrderEntity> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * Fetch orders in a given status, as a Slice (no total count query).
     * Uses idx_orders_status.
     */
    Slice<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);

    /**
     * Fetch the most recently updated orders in a given status.
     * Demonstrates a custom JPQL query with ordering.
     */
    @Query("SELECT o FROM OrderEntity o WHERE o.status = :status ORDER BY o.updatedAt DESC")
    List<OrderEntity> findTop100ByStatusOrderByUpdatedAtDesc(OrderStatus status);

    /**
     * Stream all orders that were created before a given timestamp.
     * Allows cursor‑style processing of arbitrarily large data sets.
     */
    @Query("SELECT o FROM OrderEntity o WHERE o.createdAt < :cutoff")
    Stream<OrderEntity> streamAllByCreatedAtBefore(Instant cutoff);

    /**
     * Example of a native query if you need DB‑specific optimizations.
     * Here we pull the latest 50 cancelled orders.
     */
    @Query(
            value = "SELECT * FROM orders WHERE status = 'CANCELLED' ORDER BY updated_at DESC LIMIT 50",
            nativeQuery = true
    )
    List<OrderEntity> findLatest50CancelledNative();
}
