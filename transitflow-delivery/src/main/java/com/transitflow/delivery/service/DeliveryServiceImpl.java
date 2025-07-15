package com.transitflow.delivery.service;

import com.transitflow.common.dtos.delivery.ProofOfDeliveryDto;
import com.transitflow.common.dtos.delivery.TrackingUpdateDto;
import com.transitflow.common.enmus.ShipmentStatus;
import com.transitflow.common.events.ShipmentDeliveredEvent;
import com.transitflow.common.events.ShipmentDispatchedEvent;
import com.transitflow.common.outbox.DomainEventPublisher;
import com.transitflow.delivery.domain.ShipmentEvent;
import com.transitflow.delivery.factories.DeliveryEventFactory;
import com.transitflow.delivery.repository.ShipmentEventRepository;
import com.transitflow.dispatch.domain.Shipment;
import com.transitflow.dispatch.repository.data_access_layer.ShipmentRepoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    private final ShipmentEventRepository shipmentEventRepository;
    private final ShipmentRepoService shipmentRepoService;
    private final DomainEventPublisher domainEventPublisher;
    private final DeliveryEventFactory deliveryEventFactory;
    private final TaskScheduler taskScheduler;

    /**
     * Handles shipment dispatch event.
     * Updates shipment status to IN_TRANSIT, creates tracking event,
     * and schedules automatic delivery simulation.
     */
    @Override
    @Transactional
    public void handleShipmentDispatched(ShipmentDispatchedEvent event) {
        Shipment shipment = updateShipmentStatus(event.getShipmentId(), ShipmentStatus.IN_TRANSIT);
        createTrackingEvent(shipment.getId(), ShipmentStatus.IN_TRANSIT, dispatchMetadata(event));
        scheduleDeliverySimulation(shipment.getId(), 5);
    }
    /**
     * Simulates delivery for a shipment.
     * Marks shipment as DELIVERED, logs tracking, and publishes delivery event.
     */
    @Override
    @Transactional
    public void processDelivery(Long shipmentId) {
        Shipment shipment = fetchShipment(shipmentId);
        if (isAlreadyDelivered(shipment)) return;

        updateShipmentStatusAndTime(shipment, ShipmentStatus.DELIVERED, Instant.now());
        createTrackingEvent(shipmentId, ShipmentStatus.DELIVERED, deliveryMetadata(shipmentId));
        publishDeliveryEvent(shipment);
    }

    @Override
    @Transactional
    public void markAsDelivered(Long shipmentId, ProofOfDeliveryDto proof) {
        Shipment shipment = updateShipmentStatusAndTime(fetchShipment(shipmentId), ShipmentStatus.DELIVERED, Instant.now());
        createTrackingEvent(shipmentId, ShipmentStatus.DELIVERED, proofMetadata(proof));
        publishDeliveryEvent(shipment);
    }

    /**
     * Returns chronological tracking history of a shipment.
     * Used for shipment tracking views or APIs.
     */
    @Override
    public List<TrackingUpdateDto> getTrackingHistory(Long shipmentId) {
        return shipmentEventRepository.findByShipmentIdOrderByOccurredAtAsc(shipmentId)
                .stream()
                .map(this::mapToTrackingUpdate)
                .toList();
    }

    /**
     * Creates a custom tracking event.
     * Useful for manual or external shipment status updates.
     */
    @Override
    @Transactional
    public void createTrackingUpdate(TrackingUpdateDto trackingUpdate) {
        createTrackingEvent(trackingUpdate.getShipmentId(), trackingUpdate.getEventType(), trackingUpdate.getMetadata());
    }

    /**
     * Schedules delivery simulation after a delay.
     * Automatically calls processDelivery at scheduled time.
     */
    @Override
    public void scheduleDeliverySimulation(Long shipmentId, long delayInMinutes) {
        Instant triggerTime = Instant.now().plus(Duration.ofMinutes(delayInMinutes));
        taskScheduler.schedule(() -> processDelivery(shipmentId), triggerTime);
    }

    // ============ PRIVATE HELPERS ============

    private Shipment fetchShipment(Long id) {
        return shipmentRepoService.findById(id);
    }

    private Shipment updateShipmentStatus(Long shipmentId, ShipmentStatus status) {
        Shipment shipment = fetchShipment(shipmentId);
        shipment.setStatus(status);
        return shipmentRepoService.save(shipment);
    }

    private Shipment updateShipmentStatusAndTime(Shipment shipment, ShipmentStatus status, Instant time) {
        shipment.setStatus(status);
        shipment.setDeliveredAt(time);
        return shipmentRepoService.save(shipment);
    }

    private void createTrackingEvent(Long shipmentId, ShipmentStatus status, Map<String, Object> metadata) {
        ShipmentEvent event = ShipmentEvent.builder()
                .shipmentId(shipmentId)
                .eventType(status)
                .occurredAt(Instant.now())
                .build();

        event.setMetadataMap(metadata); // ✅ Manually set metadata using custom method

        shipmentEventRepository.save(event);
    }


    private void publishDeliveryEvent(Shipment shipment) {
        ShipmentDeliveredEvent event = deliveryEventFactory.createShipmentDeliveredEvent(shipment);
        domainEventPublisher.publish(event, shipment.getId().toString(), "Shipment");
    }

    private boolean isAlreadyDelivered(Shipment shipment) {
        return shipment.getStatus() == ShipmentStatus.DELIVERED;
    }

    // ============ METADATA HELPERS ============

    private Map<String, Object> dispatchMetadata(ShipmentDispatchedEvent event) {
        return buildMetadata(map -> {
            map.put("vehicleId", event.getVehicleId());
            map.put("dispatchedAt", event.getDispatchedAt());
            map.put("estimatedDeliveryTime", Instant.now().plus(Duration.ofHours(2)).toString());
        });
    }

    private Map<String, Object> deliveryMetadata(Long shipmentId) {
        return buildMetadata(map -> {
            map.put("deliveredAt", Instant.now());
            map.put("deliveryMethod", "AUTOMATIC_SIMULATION");
            map.put("location", "Customer Address");
        });
    }

    private Map<String, Object> proofMetadata(ProofOfDeliveryDto proof) {
        return buildMetadata(map -> {
            map.put("deliveredAt", Instant.now());
            map.put("deliveryMethod", "MANUAL_CONFIRMATION");
            map.put("recipientName", proof.getRecipientName());
            map.put("deliveryPhotoUrl", proof.getDeliveryPhotoUrl());
            map.put("notes", proof.getNotes());
        });
    }

    private Map<String, Object> buildMetadata(java.util.function.Consumer<Map<String, Object>> builder) {
        Map<String, Object> metadata = new HashMap<>();
        builder.accept(metadata);
        return metadata;
    }

    private TrackingUpdateDto mapToTrackingUpdate(ShipmentEvent event) {
        TrackingUpdateDto dto = new TrackingUpdateDto();
        dto.setShipmentId(event.getShipmentId());
        dto.setEventType(event.getEventType());
        dto.setOccurredAt(event.getOccurredAt());
        dto.setMetadata(event.getMetadataMap());
        return dto;
    }
}
