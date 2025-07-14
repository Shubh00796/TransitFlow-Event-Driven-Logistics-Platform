package com.transitflow.dispatch.controllers;


import com.transitflow.common.dtos.ApiResponse;
import com.transitflow.common.dtos.disptach.ShipmentRequestDto;
import com.transitflow.common.dtos.disptach.ShipmentResponseDto;
import com.transitflow.common.enmus.ShipmentStatus;
import com.transitflow.dispatch.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<ShipmentResponseDto>> createShipment(@Valid @RequestBody ShipmentRequestDto request) {
        ShipmentResponseDto created = shipmentService.createShipment(request);
        return ResponseEntity.ok(ApiResponse.success("Shipment created successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentResponseDto>> getById(@PathVariable Long id) {
        ShipmentResponseDto shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Shipment fetched", shipment));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<ShipmentResponseDto>>> getByOrderId(@PathVariable Long orderId) {
        List<ShipmentResponseDto> shipments = shipmentService.getShipmentsByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success("Shipments by orderId", shipments));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<List<ShipmentResponseDto>>> getByVehicleId(@PathVariable Long vehicleId) {
        List<ShipmentResponseDto> shipments = shipmentService.getShipmentsByVehicleId(vehicleId);
        return ResponseEntity.ok(ApiResponse.success("Shipments by vehicleId", shipments));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Page<ShipmentResponseDto>>> getByStatus(
            @RequestParam ShipmentStatus status,
            Pageable pageable) {
        Page<ShipmentResponseDto> page = shipmentService.getShipmentsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Shipments by status", page));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<ShipmentResponseDto>>> getRecentDispatched(
            @RequestParam ShipmentStatus status) {
        List<ShipmentResponseDto> recent = shipmentService.getRecentDispatchedShipments(status);
        return ResponseEntity.ok(ApiResponse.success("Recent dispatched shipments", recent));
    }

    @PatchMapping("/{id}/deliver")
    public ResponseEntity<ApiResponse<Void>> markAsDelivered(
            @PathVariable Long id,
            @RequestParam(required = false) Instant deliveredAt) {
        shipmentService.markAsDelivered(id, deliveredAt);
        return ResponseEntity.ok(ApiResponse.success("Shipment marked as delivered", null));
    }
}
