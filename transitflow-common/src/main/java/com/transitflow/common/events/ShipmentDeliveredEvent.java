package com.transitflow.common.events;


import com.transitflow.common.enmus.ShipmentStatus;
import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShipmentDeliveredEvent {
    private Long shipmentId;
    private Long orderId;
    private Long vehicleId;
    private Instant deliveredAt;
    private ShipmentStatus status;
    private String deliveryLocation;
    private String recipientName;
    private String deliveryNotes;
    private String proofOfDeliveryUrl;

    @Override
    public String toString() {
        return "ShipmentDeliveredEvent{" +
                "shipmentId=" + shipmentId +
                ", orderId=" + orderId +
                ", vehicleId=" + vehicleId +
                ", deliveredAt=" + deliveredAt +
                ", status=" + status +
                '}';
    }}
