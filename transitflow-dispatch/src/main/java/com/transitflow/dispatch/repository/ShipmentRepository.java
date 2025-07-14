package com.transitflow.dispatch.repository;


import com.transitflow.common.enmus.ShipmentStatus;
import com.transitflow.dispatch.domain.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    List<Shipment> findByOrderId(Long orderId);

    List<Shipment> findByVehicleId(Long vehicleId);

    Page<Shipment> findAllByStatus(ShipmentStatus status, Pageable pageable);

    // Stream shipments for batch processing (e.g., all delivered today)
    @Query("SELECT s FROM Shipment s WHERE s.deliveredAt >= :from AND s.deliveredAt < :to")
    Stream<Shipment> streamDeliveredBetween(@Param("from") Instant from, @Param("to") Instant to);

    // Optional single record by composite fields
    Optional<Shipment> findByOrderIdAndVehicleId(Long orderId, Long vehicleId);

    long countByStatus(ShipmentStatus status);

    // Find latest dispatched shipments (limited)
    List<Shipment> findTop10ByStatusOrderByDispatchedAtDesc(ShipmentStatus status);
}
