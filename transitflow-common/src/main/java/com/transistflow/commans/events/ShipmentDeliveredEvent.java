package com.transistflow.commans.events;


import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class ShipmentDeliveredEvent {
    private Long shipmentId;
    private Instant deliveredAt;
    private Map<String, Object> proofOfDelivery; // recipient name, photo URL, etc.
}
