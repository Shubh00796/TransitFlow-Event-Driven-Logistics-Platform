package com.transistflow.commans.events;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentDispatchedEvent {
    private Long shipmentId;
    private Long orderId;
    private Long vehicleId;
    private Instant dispatchedAt;
}
