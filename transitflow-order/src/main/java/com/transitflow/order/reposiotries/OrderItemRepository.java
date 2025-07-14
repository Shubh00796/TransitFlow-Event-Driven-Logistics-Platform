package com.transitflow.order.reposiotries;


import com.transitflow.order.domain.OrderItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    /**
     * Fetch items for a given order, paged.
     * Uses idx_order_items_order_id.
     */
    Page<OrderItemEntity> findByOrderId(Long orderId, Pageable pageable);

    /**
     * Fetch all items for an order as a List (small result sets only).
     */
    List<OrderItemEntity> findAllByOrderId(Long orderId);

    /**
     * Stream items for an order, for large‑volume processing.
     */
    @Query("SELECT i FROM OrderItemEntity i WHERE i.orderId = :orderId")
    Stream<OrderItemEntity> streamByOrderId(Long orderId);


}
