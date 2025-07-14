package com.transitflow.dispatch.service;


import com.transistflow.commans.dtos.disptach.ShipmentRequestDto;
import com.transistflow.commans.dtos.disptach.ShipmentResponseDto;
import com.transistflow.commans.enmus.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface ShipmentService {

    ShipmentResponseDto createShipment(ShipmentRequestDto request);

    ShipmentResponseDto getShipmentById(Long id);

    List<ShipmentResponseDto> getShipmentsByOrderId(Long orderId);

    List<ShipmentResponseDto> getShipmentsByVehicleId(Long vehicleId);

    Page<ShipmentResponseDto> getShipmentsByStatus(ShipmentStatus status, Pageable pageable);

    // Get recently dispatched shipments
    List<ShipmentResponseDto> getRecentDispatchedShipments(ShipmentStatus status);

    void markAsDelivered(Long shipmentId, Instant deliveredAt);
}
