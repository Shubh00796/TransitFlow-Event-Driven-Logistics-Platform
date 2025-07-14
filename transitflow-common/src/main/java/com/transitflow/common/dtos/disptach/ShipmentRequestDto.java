package com.transitflow.common.dtos.disptach;


import com.transitflow.common.enmus.ShipmentStatus;
import lombok.Data;

@Data
public class ShipmentRequestDto {
    private Long orderId;
    private Long vehicleId;
    private ShipmentStatus status;

}
