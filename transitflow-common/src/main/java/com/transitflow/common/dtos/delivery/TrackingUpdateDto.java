package com.transitflow.common.dtos.delivery;


import lombok.Data;
import java.time.Instant;
import java.util.Map;

@Data
public class TrackingUpdateDto {
    private Long shipmentId;
    private String eventType; // e.g., SHIPMENT_DISPATCHED
    private Instant occurredAt;
    private Map<String, Object> metadata;
}
