package com.transistflow.commans.events;


import lombok.Data;

import java.time.Instant;

@Data
public class ShipmentDispatchedEvent {
    private Long shipmentId;
    private Long orderId;
    private Long vehicleId;
    private Instant dispatchedAt;
}
