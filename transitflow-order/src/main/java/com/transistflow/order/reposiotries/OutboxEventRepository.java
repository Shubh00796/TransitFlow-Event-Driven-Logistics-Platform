package com.transistflow.order.reposiotries;

import com.transistflow.commans.enmus.OutboxStatus;
import com.transistflow.order.domain.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    // Fetch 100 pending events, oldest first
    List<OutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);

    // Update event status
    @Modifying
    @Query("UPDATE OutboxEvent e SET e.status = :status, e.updatedAt = :updatedAt WHERE e.id = :id")
    void updateStatusById(@Param("id") UUID id, @Param("status") OutboxStatus status, @Param("updatedAt") LocalDateTime updatedAt);

    // Delete old published events
    @Modifying
    @Query("DELETE FROM OutboxEvent e WHERE e.status = :status AND e.createdAt < :cutoff")
    void deleteByStatusAndCreatedAtBefore(@Param("status") OutboxStatus status, @Param("cutoff") LocalDateTime cutoff);

    // idempotency guard: has this aggregateId & eventType been recorded?
    boolean existsByAggregateIdAndType(String aggregateId, String type);
}
