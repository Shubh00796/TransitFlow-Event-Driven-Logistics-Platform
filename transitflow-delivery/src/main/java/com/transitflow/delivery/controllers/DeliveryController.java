package com.transitflow.delivery.controllers;


import com.transitflow.common.dtos.ApiResponse;
import com.transitflow.common.dtos.delivery.ProofOfDeliveryDto;
import com.transitflow.common.dtos.delivery.TrackingUpdateDto;
import com.transitflow.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    /**
     * Endpoint to mark a shipment as delivered manually with proof.
     */
    @PostMapping("/mark-delivered/{shipmentId}")
    public ResponseEntity<ApiResponse<Void>> markAsDelivered(
            @PathVariable Long shipmentId,
            @Valid @RequestBody ProofOfDeliveryDto proof
    ) {
        deliveryService.markAsDelivered(shipmentId, proof);
        return ResponseEntity.ok(ApiResponse.success("Shipment marked as delivered", null));
    }

    /**
     * Endpoint to simulate delivery after a delay (for testing or automation).
     */
    @PostMapping("/simulate-delivery/{shipmentId}")
    public ResponseEntity<ApiResponse<Void>> simulateDelivery(
            @PathVariable Long shipmentId,
            @RequestParam(defaultValue = "5") long delayInMinutes
    ) {
        deliveryService.scheduleDeliverySimulation(shipmentId, delayInMinutes);
        return ResponseEntity.ok(ApiResponse.success("Delivery simulation scheduled", null));
    }

    /**
     * Endpoint to manually create a tracking update.
     */
    @PostMapping("/tracking")
    public ResponseEntity<ApiResponse<Void>> createTrackingEvent(
            @Valid @RequestBody TrackingUpdateDto trackingUpdate
    ) {
        deliveryService.createTrackingUpdate(trackingUpdate);
        return ResponseEntity.ok(ApiResponse.success("Tracking event created", null));
    }

    /**
     * Endpoint to retrieve tracking history for a shipment.
     */
    @GetMapping("/tracking/{shipmentId}")
    public ResponseEntity<ApiResponse<List<TrackingUpdateDto>>> getTrackingHistory(
            @PathVariable Long shipmentId
    ) {
        List<TrackingUpdateDto> history = deliveryService.getTrackingHistory(shipmentId);
        return ResponseEntity.ok(ApiResponse.success("Tracking history retrieved", history));
    }
}
