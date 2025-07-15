package com.transitflow.delivery.service;


import com.transitflow.common.dtos.delivery.ProofOfDeliveryDto;
import com.transitflow.common.dtos.delivery.TrackingUpdateDto;
import com.transitflow.common.events.ShipmentDispatchedEvent;

import java.util.List;

public interface DeliveryService {

    /**
     * Handles shipment dispatch event from Kafka
     * Updates shipment status to IN_TRANSIT and creates tracking event
     */
    void handleShipmentDispatched(ShipmentDispatchedEvent event);

    /**
     * Simulates delivery process and marks shipment as delivered
     */
    void processDelivery(Long shipmentId);

    /**
     * Marks shipment as delivered with proof of delivery
     */
    void markAsDelivered(Long shipmentId, ProofOfDeliveryDto proofOfDelivery);

    /**
     * Gets tracking history for a shipment
     */
    List<TrackingUpdateDto> getTrackingHistory(Long shipmentId);

    /**
     * Creates a tracking update event
     */
    void createTrackingUpdate(TrackingUpdateDto trackingUpdate);

    /**
     * Schedules automatic delivery simulation
     */
    void scheduleDeliverySimulation(Long shipmentId, long delayInMinutes);
}