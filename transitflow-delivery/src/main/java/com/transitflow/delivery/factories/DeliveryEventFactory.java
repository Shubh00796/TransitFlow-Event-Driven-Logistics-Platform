package com.transitflow.delivery.factories;

import com.transitflow.common.events.ShipmentDeliveredEvent;
import com.transitflow.dispatch.domain.Shipment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
@Slf4j
public class DeliveryEventFactory {

    /**
     * Creates a ShipmentDeliveredEvent from a delivered shipment
     */
    public ShipmentDeliveredEvent createShipmentDeliveredEvent(Shipment shipment) {
        log.debug("Creating ShipmentDeliveredEvent for shipment: {}",
                safeGet(shipment, Shipment::getId));

        validateShipment(shipment);

        return ShipmentDeliveredEvent.builder()
                .shipmentId(safeGet(shipment, Shipment::getId))
                .orderId(safeGet(shipment, Shipment::getOrderId))
                .vehicleId(safeGet(shipment, Shipment::getVehicleId))
                .deliveredAt(Optional.ofNullable(shipment.getDeliveredAt()).orElseGet(Instant::now))
                .status(shipment.getStatus())
                .build();
    }

    /**
     * Central validation logic for shipment
     */
    private void validateShipment(Shipment shipment) {
        validateNotNull(shipment, "Shipment cannot be null");

        validateWith(shipment, s -> s.getId() != null, "Shipment ID cannot be null");
        validateWith(shipment, s -> s.getOrderId() != null, "Order ID cannot be null");
        validateWith(shipment, s -> s.getVehicleId() != null, "Vehicle ID cannot be null");

        log.debug("Shipment validation passed: {}", shipment.getId());
    }

    /**
     * Generic validator using Predicate
     */
    private <T> void validateWith(T target, Predicate<T> condition, String errorMessage) {
        if (!condition.test(target)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Null check wrapper
     */
    private <T> void validateNotNull(T obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Safe getter using functional interface
     */
    private <T, R> R safeGet(T obj, Function<T, R> extractor) {
        validateNotNull(obj, "Target object is null");
        R value = extractor.apply(obj);
        validateNotNull(value, "Extracted value is null");
        return value;
    }
}
