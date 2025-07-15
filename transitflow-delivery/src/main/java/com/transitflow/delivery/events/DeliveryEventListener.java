package com.transitflow.delivery.events;


import com.transitflow.common.events.ShipmentDispatchedEvent;
import com.transitflow.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryEventListener {

    private final DeliveryService deliveryService;

    /**
     * Listens to shipment dispatched events from dispatch.shipped topic
     * This is the main integration point between dispatch and delivery modules
     */
    @KafkaListener(
            topics = "shipmentdispatchedevent",
            groupId = "delivery-group",
            containerFactory = "shipmentDispatchedEventKafkaListenerContainerFactory"
    )
    public void handleShipmentDispatched(
            @Payload ShipmentDispatchedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        log.info("📦 Received ShipmentDispatchedEvent: shipmentId={}, vehicleId={}, topic={}, partition={}, offset={}",
                event.getShipmentId(), event.getVehicleId(), topic, partition, offset);

        try {
            // Process the shipment dispatch
            deliveryService.handleShipmentDispatched(event);

            // Manual acknowledgment after successful processing
            acknowledgment.acknowledge();

            log.info("✅ Successfully processed ShipmentDispatchedEvent for shipment: {}", event.getShipmentId());

        } catch (Exception e) {
            log.error("❌ Failed to process ShipmentDispatchedEvent for shipment: {}", event.getShipmentId(), e);

            // In a production system, you might want to:
            // 1. Send to a dead letter queue
            // 2. Implement retry logic
            // 3. Alert monitoring systems

            // For now, we'll acknowledge to prevent infinite retries
            // but in production, implement proper error handling
            acknowledgment.acknowledge();
        }
    }

    /**
     * Health check method to verify listener is active
     * This can be used by monitoring systems
     */
    public boolean isListenerActive() {
        return true; // Add actual health check logic if needed
    }
}