package com.transitflow.delivery.repository;


import com.transitflow.delivery.domain.ShipmentEvent;
import com.transitflow.common.enmus.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentEventRepository extends JpaRepository<ShipmentEvent, Long> {

    List<ShipmentEvent> findByShipmentIdOrderByOccurredAtAsc(Long shipmentId);

    List<ShipmentEvent> findByEventTypeOrderByOccurredAtDesc(ShipmentStatus eventType);

    Optional<ShipmentEvent> findTopByShipmentIdOrderByOccurredAtDesc(Long shipmentId);

    @Query("SELECT se FROM ShipmentEvent se WHERE se.shipmentId = :shipmentId " +
            "AND se.eventType = :eventType ORDER BY se.occurredAt DESC")
    Optional<ShipmentEvent> findLatestEventByShipmentAndType(
            @Param("shipmentId") Long shipmentId,
            @Param("eventType") ShipmentStatus eventType);

    @Query("SELECT se FROM ShipmentEvent se WHERE se.occurredAt BETWEEN :startTime AND :endTime " +
            "ORDER BY se.occurredAt DESC")
    List<ShipmentEvent> findEventsInTimeRange(
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime);

    boolean existsByShipmentIdAndEventType(Long shipmentId, ShipmentStatus eventType);
}