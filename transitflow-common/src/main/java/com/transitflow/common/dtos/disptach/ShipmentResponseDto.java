package com.transitflow.common.dtos.disptach;


import lombok.Data;
import java.time.Instant;

@Data
public class ShipmentResponseDto {
    private Long id;
    private Long orderId;
    private Long vehicleId;
    private String status;
    private Instant dispatchedAt;
    private Instant deliveredAt;
}
