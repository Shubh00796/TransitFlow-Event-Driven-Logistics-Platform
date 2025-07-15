package com.transitflow.delivery.service;


import com.transitflow.common.dtos.delivery.ProofOfDeliveryDto;
import com.transitflow.common.dtos.delivery.TrackingUpdateDto;
import com.transitflow.common.enmus.ShipmentStatus;
import com.transitflow.common.events.ShipmentDispatchedEvent;
import com.transitflow.common.events.ShipmentDeliveredEvent;
import com.transitflow.common.outbox.DomainEventPublisher;
import com.transitflow.delivery.domain.ShipmentEvent;
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
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    private final ShipmentEventRepository shipmentEventRepository;
    private final ShipmentRepoService shipmentRepoService;
    private final DomainEventPublisher domainEventPublisher;
    private final DeliveryEventFactory deliveryEventFactory;
    private final TaskScheduler taskScheduler;

    @Override
    @Transactional
    public void handleShipmentDispatched(ShipmentDispatchedEvent event) {
        log.info("Processing shipment dispatch event for shipment ID: {}", event.getShipmentId());

        try {
            // Update shipment status to IN_TRANSIT
            Shipment shipment = shipmentRepoService.findById(event.getShipmentId());
            shipment.setStatus(ShipmentStatus.);
            shipmentRepoService.save(shipment);

            // Create tracking event
            createTrackingEvent(event.getShipmentId(), ShipmentStatus.IN_TRANSIT,
                    createDispatchMetadata(event));

            // Schedule automatic delivery simulation (e.g., 5 minutes delay)
            scheduleDeliverySimulation(event.getShipmentId(), 5);

            log.info("Successfully processed dispatch event for shipment: {}", event.getShipmentId());

        } catch (Exception e) {
            log.error("Failed to process dispatch event for shipment: {}", event.getShipmentId(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void processDelivery(Long shipmentId) {
        log.info("Processing delivery for shipment ID: {}", shipmentId);

        Shipment shipment = shipmentRepoService.findById(shipmentId);

        // Check if already delivered
        if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
            log.warn("Shipment {} is already delivered", shipmentId);
            return;
        }

        // Mark as delivered
        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setDeliveredAt(Instant.now());
        shipmentRepoService.save(shipment);

        // Create delivery tracking event
        createTrackingEvent(shipmentId, ShipmentStatus.DELIVERED,
                createDeliveryMetadata(shipmentId));

        // Publish delivery event
        publishDeliveryEvent(shipment);

        log.info("Successfully processed delivery for shipment: {}", shipmentId);
    }

    @Override
    @Transactional
    public void markAsDelivered(Long shipmentId, ProofOfDeliveryDto proofOfDelivery) {
        log.info("Marking shipment {} as delivered with proof", shipmentId);

        Shipment shipment = shipmentRepoService.findById(shipmentId);
        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setDeliveredAt(Instant.now());
        shipmentRepoService.save(shipment);

        // Create delivery tracking event with proof details
        Map<String, Object> metadata = createProofOfDeliveryMetadata(proofOfDelivery);
        createTrackingEvent(shipmentId, ShipmentStatus.DELIVERED, metadata);

        // Publish delivery event
        publishDeliveryEvent(shipment);
    }

    @Override
    public List<TrackingUpdateDto> getTrackingHistory(Long shipmentId) {
        List<ShipmentEvent> events = shipmentEventRepository.findByShipmentIdOrderByOccurredAtAsc(shipmentId);

        return events.stream()
                .map(this::mapToTrackingUpdate)
                .toList();
    }

    @Override
    @Transactional
    public void createTrackingUpdate(TrackingUpdateDto trackingUpdate) {
        createTrackingEvent(trackingUpdate.getShipmentId(),
                trackingUpdate.getEventType(),
                trackingUpdate.getMetadata());
    }

    @Override
    public void scheduleDeliverySimulation(Long shipmentId, long delayInMinutes) {
        log.info("Scheduling delivery simulation for shipment {} in {} minutes", shipmentId, delayInMinutes);

        Instant deliveryTime = Instant.now().plus(Duration.ofMinutes(delayInMinutes));

        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(() -> {
            try {
                processDelivery(shipmentId);
            } catch (Exception e) {
                log.error("Failed to process scheduled delivery for shipment: {}", shipmentId, e);
            }
        }, deliveryTime);

        log.info("Scheduled delivery simulation for shipment: {} at {}", shipmentId, deliveryTime);
    }

    // ========== PRIVATE HELPER METHODS ==========

    private void createTrackingEvent(Long shipmentId, ShipmentStatus eventType, Map<String, Object> metadata) {
        ShipmentEvent event = ShipmentEvent.builder()
                .shipmentId(shipmentId)
                .eventType(eventType)
                .occurredAt(Instant.now())
                .build();

        if (metadata != null && !metadata.isEmpty()) {
            event.setMetadataMap(metadata);
        }

        shipmentEventRepository.save(event);
        log.debug("Created tracking event: {} for shipment: {}", eventType, shipmentId);
    }

    private Map<String, Object> createDispatchMetadata(ShipmentDispatchedEvent event) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("vehicleId", event.getVehicleId());
        metadata.put("dispatchedAt", event.getDispatchedAt());
        metadata.put("estimatedDeliveryTime", calculateEstimatedDeliveryTime());
        return metadata;
    }

    private Map<String, Object> createDeliveryMetadata(Long shipmentId) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("deliveredAt", Instant.now());
        metadata.put("deliveryMethod", "AUTOMATIC_SIMULATION");
        metadata.put("location", "Customer Address");
        return metadata;
    }

    private Map<String, Object> createProofOfDeliveryMetadata(ProofOfDeliveryDto proof) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("deliveredAt", Instant.now());
        metadata.put("deliveryMethod", "MANUAL_CONFIRMATION");
        metadata.put("recipientName", proof.getRecipientName());
        metadata.put("deliveryPhotoUrl", proof.getDeliveryPhotoUrl());
        metadata.put("notes", proof.getNotes());
        return metadata;
    }

    private void publishDeliveryEvent(Shipment shipment) {
        ShipmentDeliveredEvent event = deliveryEventFactory.createShipmentDeliveredEvent(shipment);
        domainEventPublisher.publish(event, shipment.getId().toString(), "Shipment");
        log.info("Published delivery event for shipment: {}", shipment.getId());
    }

    private TrackingUpdateDto mapToTrackingUpdate(ShipmentEvent event) {
        TrackingUpdateDto dto = new TrackingUpdateDto();
        dto.setShipmentId(event.getShipmentId());
        dto.setEventType(event.getEventType());
        dto.setOccurredAt(event.getOccurredAt());
        dto.setMetadata(event.getMetadataMap());
        return dto;
    }

    private String calculateEstimatedDeliveryTime() {
        // Simple calculation - add 2 hours to current time
        return Instant.now().plus(Duration.ofHours(2)).toString();
    }
}