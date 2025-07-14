package com.transitflow.dispatch.utils;


import com.transitflow.common.events.ShipmentDispatchedEvent;
import com.transitflow.dispatch.domain.Shipment;
import org.springframework.stereotype.Component;

@Component
public class ShipmentEventFactory {

    public ShipmentDispatchedEvent shipmentDispatchedEvent(Shipment shipment) {
        return new ShipmentDispatchedEvent(
                shipment.getId(),
                shipment.getOrderId(),
                shipment.getVehicleId(),
                shipment.getDispatchedAt()
        );
    }
}
