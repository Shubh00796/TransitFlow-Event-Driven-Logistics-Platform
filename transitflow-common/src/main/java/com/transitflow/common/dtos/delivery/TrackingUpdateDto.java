package com.transitflow.common.dtos.delivery;


import com.transitflow.common.enmus.ShipmentStatus;
import lombok.Data;
import java.time.Instant;
import java.util.Map;

@Data
public class TrackingUpdateDto {
    private Long shipmentId;
    private ShipmentStatus eventType; // e.g., SHIPMENT_DISPATCHED
    private Instant occurredAt;
    private Map<String, Object> metadata;
}
